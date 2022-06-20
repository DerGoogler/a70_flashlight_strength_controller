#include <jni.h>
#include <iostream>
#include <stdexcept>
#include <cstdio>
#include <string>
#include <sstream>

using namespace std;

void addToStream(ostringstream &) {}

template<typename T, typename... Args>
void addToStream(ostringstream &a_stream, T &&a_value, Args &&... a_args) {
    a_stream << forward<T>(a_value);
    addToStream(a_stream, forward<Args>(a_args)...);
}

template<typename... Args>
string concat(Args &&... a_args) {
    ostringstream s;
    addToStream(s, forward<Args>(a_args)...);
    return s.str();
}

string ConvertJString(JNIEnv *env, jstring str) {
    if (!str) string();
    const jsize len = env->GetStringUTFLength(str);
    const char *strChars = env->GetStringUTFChars(str, (jboolean *) 0);
    string Result(strChars, len);
    env->ReleaseStringUTFChars(str, strChars);
    return Result;
}

JNIEXPORT jstring JNICALL StringTest(JNIEnv *env) {
    const char *test = "something";
    return env->NewStringUTF(test);
}

string exec(const char *cmd) {
    char buffer[128];
    string result;
    FILE *pipe = popen(concat("su -c ", cmd).c_str(), "r");
    if (!pipe) throw std::runtime_error("popen() failed!");
    try {
        while (fgets(buffer, sizeof buffer, pipe) != nullptr) {
            result += buffer;
            return result;
        }
    } catch (...) {
        pclose(pipe);
        throw;
    }
    pclose(pipe);
    return result;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_dergoogler_phh_flashlight_util_Shell_exec(JNIEnv *env, jclass clazz, jstring command) {
    string cmd = ConvertJString(env, command);
    exec(cmd.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_dergoogler_phh_flashlight_util_Shell_resultOf(JNIEnv *env, jclass clazz, jstring command) {
    string cmd = ConvertJString(env, command);
    string result = exec(cmd.c_str());
    return env->NewStringUTF(result.c_str());
}