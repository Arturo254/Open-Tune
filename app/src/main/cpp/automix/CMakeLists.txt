cmake_minimum_required(VERSION 3.22.1)
project(exhale_analysis LANGUAGES C CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Aseguramos que CMake busque los archivos usando la ruta absoluta de esta carpeta
add_library(exhale_analysis SHARED
    ${CMAKE_CURRENT_SOURCE_DIR}/audio_analysis.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/tempo_analysis.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/resampler.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/jni/analysis_jni.cpp
)

target_include_directories(exhale_analysis PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}
)

target_compile_options(exhale_analysis PRIVATE -O3 -fvisibility=hidden)

find_library(log-lib log)
target_link_libraries(exhale_analysis ${log-lib})

target_link_options(exhale_analysis PRIVATE "-Wl,-z,max-page-size=16384")