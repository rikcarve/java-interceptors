[![Build Status][ci-img]][ci] [![Released Version][maven-img]][maven]

# OpenTracing Java Interceptors instrumentation

This library provides a Jakarta EE [Interceptor](https://jakarta.ee/specifications/platform/8/apidocs/javax/interceptor/Interceptor.html) that will add a new Span for any [`@Traced`](https://github.com/eclipse/microprofile-opentracing/blob/master/spec/src/main/asciidoc/microprofile-opentracing.asciidoc#the-traced-annotation)-annotated method. See the [Eclipse MicroProfile OpenTracing](https://github.com/eclipse/microprofile-opentracing/) for details.

## Development
```bash
./gradlew check
```

## Release
Follow instructions in [RELEASE](RELEASE.md)

   [ci-img]: https://travis-ci.org/opentracing-contrib/java-interceptors.svg?branch=master
   [ci]: https://travis-ci.org/opentracing-contrib/java-interceptors
   [maven-img]: https://api.bintray.com/packages/opentracing/maven/java-interceptors/images/download.svg
   [maven]: https://bintray.com/opentracing/maven/java-interceptors/_latestVersion
