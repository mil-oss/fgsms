# Smoke Tests

This is a test suite that must be ran independent of the build.

Before starting, edit the test.properties file. This defines endpoints for the many integration points of FGSMS. In many cases, you'll need to manually download, setup and run server software in order for the test run. Some tests are operating system dependent.

1. Start tomcat
2. run `mvn install` in this directory
