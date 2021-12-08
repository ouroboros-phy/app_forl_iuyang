#include <jni.h>
#include <string>
#include "include/ncnn/net.h"
#include "include/model32.id.h"
#include "iostream"
#include "stdio.h"
static ncnn::UnlockedPoolAllocator g_blob_pool_allocator;
static ncnn::PoolAllocator g_workspace_pool_allocator;

static ncnn::Mat ncnn_param;
static ncnn::Mat ncnn_bin;

static ncnn::Net ncnn_net;

extern "C" {
JNIEXPORT jboolean JNICALL
Java_com_example_myapplication_NcnnJni_Init(JNIEnv *env, jobject thiz, jbyteArray param, jbyteArray bin) {
    // init param
    {
        int len = env->GetArrayLength(param);
        ncnn_param.create(len, (size_t) 1u);
        env->GetByteArrayRegion(param, 0, len, (jbyte *) ncnn_param);
        int ret = ncnn_net.load_param((const unsigned char *) ncnn_param);
        __android_log_print(ANDROID_LOG_DEBUG, "NcnnJni", "load_param %d %d", ret, len);
    }

    // init bin
    {
        int len = env->GetArrayLength(bin);
        ncnn_bin.create(len, (size_t) 1u);
        env->GetByteArrayRegion(bin, 0, len, (jbyte *) ncnn_bin);
        int ret = ncnn_net.load_model((const unsigned char *) ncnn_bin);
        __android_log_print(ANDROID_LOG_DEBUG, "NcnnJni", "load_model %d %d", ret, len);
    }

  /*  ncnn::Option opt;
    opt.lightmode = true;
    opt.num_threads = 4;
    opt.blob_allocator = &g_blob_pool_allocator;
    opt.workspace_allocator = &g_workspace_pool_allocator;

    ncnn::set_default_option*/
    return JNI_TRUE;
}
JNIEXPORT jfloatArray  JNICALL
Java_com_example_myapplication_NcnnJni_Detect(JNIEnv *env, jobject thiz, jobject bitmap) {
    // ncnn from bitmap
    ncnn::Mat in;
    {
        AndroidBitmapInfo info;
        AndroidBitmap_getInfo(env, bitmap, &info);
        int width = info.width;
        int height = info.height;
        if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888)
            return NULL;

        void *indata;
        AndroidBitmap_lockPixels(env, bitmap, &indata);
        // 把像素转换成data，并指定通道顺序
        in = ncnn::Mat::from_pixels((const unsigned char *) indata, ncnn::Mat::PIXEL_RGBA2BGR,
                                    width, height);

        AndroidBitmap_unlockPixels(env, bitmap);
    }

    // ncnn_net
    std::vector<float> cls_scores;
    {
        // 减去均值和乘上比例
        const float norm_255[3] = {1/255.0f, 1/255.0f, 1/255.0f};
        in.substract_mean_normalize(0, norm_255);



        ncnn::Extractor ex = ncnn_net.create_extractor();
        ex.input(model32_param_id::BLOB_inputs, in);

        ncnn::Mat out;
        // 如果时不加密是使用ex.extract("prob", out);
        ex.extract(model32_param_id::BLOB_out, out);

        int output_wsize = out.w;
        int output_hsize = out.h;
        jfloat  *output[output_wsize * output_hsize];
        __android_log_print(ANDROID_LOG_DEBUG, "NcnnJni", "channel%d", in.c);

        //输出整理
        // float类型
        for(int i = 0; i< out.h; i++) {
            for (int j = 0; j < out.w; j++) {
                output[i*output_wsize + j] = &out.row(i)[j];
            }
        }
        //建立float数组 长度为 output_wsize * output_hsize,如果只是ouput_size相当于只有一行的out的数据那就是一个object检测数据
        jfloatArray jOutputData = env->NewFloatArray(output_wsize * output_hsize);
        if (jOutputData == nullptr) return nullptr;

        env->SetFloatArrayRegion(jOutputData, 0,  output_wsize * 1,
                                 reinterpret_cast<const jfloat *>(*output));
        return jOutputData;
    }
}
}

