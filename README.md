mtaaas
======

### Overview

A Multi-Tenant Authorization as a Service (MTAaaS) prototype using Sun XACML implementation.

Functional modules:
```
PDPServer   --  A multi-threading policy decision point (PDP) can be configured to use various policy sets.
PEPClient   --  A distributed policy enforcement point (PEP) can send requests to PDP Server for access decisions.
```
For more practical deployment, the PEPClient can be replaced by the following.
```
CloudSvcPEP --  A PEP runs with the cloud service receiving requests from the client and enforce access according to the response from the PDP.
Client      --  A cloud service client requesting access to cloud resource.
``` 

### How to Run?

Change the IP addresses of PDPs in batch_tests.sh and PEPs in utils.sh to your deployment. Then run:
```
$ ./batch_tests.sh
```
The tests will automatically run and the results will be stored in pdpresults and pepresults. Some other changes may apply to the scripts before successful runs.



> **Note:** If error message "Exception: null" constantly prompted when startpdp.sh is run, check if there are multiple servers running on the same port. If so, use:
```
$ killall ant
```

> **Note:** If error message "Unsupported major.minor version 51.0" shows, set the jdk and jre version to 7.
In Ubuntu:
```
$ sudo apt-get install openjdk-7-jdk
$ sudo update-alternatives --config java
```
