include("lib")
// This is a silly hack to include shared code from the library into the buildSrc project
project(":lib").projectDir = file("../lib")
