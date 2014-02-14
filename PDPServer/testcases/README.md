Test Cases
=========================
The policy specifications are written in the extensible access control markup language (XACML). The normative specifica- tion of RBAC policies with XACML2.0 language has been proposed by OASIS XACML TC [1](https://www.oasis-open.org/committees/tc_home.php?wg_abbrev=xacml).

### MT-RBAC Policy Specification

### MTAS Policy Specification
There are three kinds of policysets: Role PolicySet (RPS), Permission PolicySet (PPS), and Trust PolicySet (TPS). The policy authorization flow can be expressed by the following:

```

RPS:i1:role --> TPS:i1              # maintained by i1
RPS:i2:role --> TPS:i2              # maintained by i2

TPS:i1 --> i1:resource --> PPS:i1   # maintained by i1
TPS:i1 --> i2:resource --> PPS:i2   # maintained by i2
TPS:i2 --> i2:resource --> PPS:i2   # maintained by i2

PPS:i1 --> i1:resource --> permit   # access granted
      \--> TPS:i1                   # looking for junior roles (maybe from i2)
PPS:i2 --> i2:resource --> permit   # access granted
      \--> TPS:i2                   # looking for junior roles

```

#### Test case Design
Assume t1 specified a trust relation: t1 trusts t2 and a user from t1 requests to access resources in t2.

```

Policy.xml
>>> subj:t1 --> TPS:t1
>>> subj:t2 --> TPS:t2

TPS:t1            # maintained by t2
>>> subj:t1 --> RPS:t1
# trust relation: t1 trusts t2
>>> subj:t1 --> RPS:t2

TPS:t2            # maintained by t2
>>> subj:t2 --> RPS:t2

RPS:t1            # maintained by t1
>>> role:t1:manager --> PPS:t1:manager
>>> role:t1:manager --> RPS:t1:employee
>>> role:t1:employee --> PPS:t1:employee

RPS:t2            # maintained by t2
# cross-tenant permission assignment -- t1:manager has t2:managers permissions
>>> role:t1:manager --> PPS:t2:manager
# cross-tenant role hierarchy assignment -- t2:manager << t1:manager
>>> role:t1:manager --> RPS:t2:manager
>>> role:t2:manager --> PPS:t2:manager
>>> role:t2:manager --> RPS:t2:employee
>>> role:t2:employee --> PPS:t2:employee

PPS:t1: ... ...
PPS:t2: ... ...

```

### Test Policy Catalog

| File								| Description					|
|-----------------------------------|-------------------------------|
| MT-RBAC001Policy.xml 				| RPS:t1:manager --> TPS:t1<br/> RPS:t1:employee --> TPS:t1 	|
| MT-RBAC001PPSt1employee.xml 		| PPS:t1:employee --> [create] purchase order |
| MT-RBAC001PPSt1manager.xml 		| PPS:t1:manager --> [sign] purchase order <br/> PPS:t1:manager --> PPS:t1:employee |
| MT-RBAC001PPSt2employee.xml 		| PPS:t2:employee --> [create] purchase order |
| MT-RBAC001PPSt2manager.xml 		| PPS:t2:manager --> [sign] purchase order <br/> PPS:t2:manager --> PPS:t2:employee |
| MT-RBAC001Request.xml 			| Anne (t1:manager) --> [create] purchase order in t1 |
| MT-RBAC001Response.xml 			| Permit |
| MT-RBAC001TPSt1.xml 				| TPS:t2 and TPS:t1:manager --> PPS:t1:manager |
| MT-RBAC001TPSt2.xml 				| TPS:t1:manager --> PPS:t1:manager<br/> TPS:t2:manager --> PPS:t2:manager<br/> TPS:t2:employee --> PPS:t2:employee
| MT-RBAC002Policy.xml 				| RPS:t1:manager --> TPS:t1<br/> RPS:t1:employee --> TPS:t1 <br/> RPS:t1:resource --> TPS:t1 |
| MT-RBAC002PPSt1employee.xml 		| PPS:t1:employee --> [create] purchase order |
| MT-RBAC002PPSt1manager.xml 		| PPS:t1:manager --> [sign] purchase order <br/> PPS:t1:manager --> PPS:t1:employee |
| MT-RBAC002PPSt2employee.xml 		| PPS:t2:employee --> [create] purchase order |
| MT-RBAC002PPSt2manager.xml 		| PPS:t2:manager --> [sign] purchase order <br/> PPS:t2:manager --> PPS:t2:employee |
| MT-RBAC002Request.xml 			| Anne (t2:manager) --> [create] purchase order in t1 |
| MT-RBAC002Response.xml 			| Permit |
| MT-RBAC002TPSt1.xml 				| TPS:t1 and TPS:t1:manager --> PPS:t1:manager<br/> TPS:t1 and TPS:t1:employee --> PPS:t1:employee<br/> TPS:t2 and TPS:t1:employee --> PPS:t1:employee<br/> TPS:t2:manager and TPS:t1:resource --> PPS:t1:manager|
| MT-RBAC003Policy.xml 				| RPS:t1:manager --> TPS:t1<br/> RPS:t1:employee --> TPS:t1<br/> RPS:t1:employee --> TPS:t1<br/> RPS:t1:resource --> TPS:t1 |
| MT-RBAC003PPSt1employee.xml 		| PPS:t1:employee --> [create] purchase order `FIXME: add in t1` |
| MT-RBAC003PPSt1manager.xml 		| PPS:t1:manager --> [sign] purchase order `FIXME: add in t1`<br/> PPS:t1:manager --> TIPRS:t1:employee and TIPRPS:t1:user:t1:employee |
| MT-RBAC003PPSt2employee.xml 		| PPS:t2:employee --> [create] purchase order |
| MT-RBAC003PPSt2manager.xml 		| PPS:t2:manager --> [sign] purchase order <br/> PPS:t2:manager --> PPS:t2:employee |
| MT-RBAC003Request.xml 			| Anne (t2:manager) --> [create] purchase order in t1 |
| MT-RBAC003Response.xml 			| Permit |
| MT-RBAC003TIPRPSt1.xml 			| TIPRPS:t1:employee --> PPS:t1:employee<br/> TIPRPS:t1:manager --> PPS:t1:manager<br/> TIPRPS:t1:user:t1:employee and subj:t1 --> PPS:t1:employee<br/> TIPRPS:t1:user:t1:manager and subj:t1 --> PPS:t1:manager |
| MT-RBAC003TPSt1.xml 				| TPS:t1 and TPS:t1:manager --> TIPRPS:t1:user:t1:manager <br/> TPS:t1 and TPS:t1:employee --> TIPRPS:t1:user:t1:employee <br/> TPS:t2 and TPS:t1:employee --> TIPRPS:t1:manager <br/> TPS:t2 and TPS:t1:employee --> TIPRPS:t1:employee <br/> TPS:t2:manager and TPS:t1:resource --> TIPRPS:t1:manager|
| MT-RBAC004Policy.xml 				| RPS:t1:manager --> TPS:t1<br/> RPS:t1:employee --> TPS:t1<br/> RPS:t1:employee --> TPS:t1<br/> RPS:t1:resource --> TPS:t1 |
| MT-RBAC004PPSt1employee.xml 		| PPS:t1:employee --> [create] purchase order |
| MT-RBAC004PPSt1manager.xml 		| PPS:t1:manager --> [sign] purchase order <br/> PPS:t1:manager --> TDPRS:t1:employee and TDPRPS:t1:user:t1:employee |
| MT-RBAC004PPSt2employee.xml 		| PPS:t2:employee --> [create] purchase order |
| MT-RBAC004PPSt2manager.xml 		| PPS:t2:manager --> [sign] purchase order <br/> PPS:t2:manager --> PPS:t2:employee |
| MT-RBAC004Request.xml 			| Anne (t2:manager) --> [create] purchase order in t1 |
| MT-RBAC004Response.xml 			| Permit |
| MT-RBAC004TDPRPSt1.xml `FIXME`			| TDPRPS:t1:employee:t2 --> PPS:t1:employee<br/> TDPRPS:t1:manager and TDPRPS:t2 --> PPS:t1:manager<br/>TDPRPS:t1:user:t1:employee --> PPS:t1:employee<br/> TDPRPS:t1:user:t1:manager --> PPS:t1:manager|
| MT-RBAC004TPSt1.xml 				| TPS:t1 and TPS:t1:manager --> TDPRPS:t1:user:t1:manager<br/> TPS:t1 and TPS:t1:employee --> TDPRPS:t1:user:t1:employee<br/> TPS:t2 and TPS:t1:manager --> TDPRPS:t1:manager<br/> TPS:t2 and TPS:t1:employee --> TDPRPS:t1:employee<br/> TPS:t2:manager and TPS:t1:resource --> TDPRPS:t1:manager|
| MT-RBACPolicy.xml           | RPS:t1:manager --> TPS:t1<br/> RPS:t1:employee --> TPS:t1<br/> RPS:t1:resource --> TPS:t1 |
| MT-RBACPPSt1employee.xml    | PPS:t1:employee --> [create] purchase order |
| MT-RBACPPSt1manager.xml     | PPS:t1:manager --> [sign] purchase order <br/> PPS:t1:manager --> TDPRPS:t1:employee | 
| MT-RBACRequest.xml          | Anne (t2:manager) --> [create] purchase order in t1 |
| MT-RBACResponse.xml         | Permit |
| MT-RBACTDPRPSt1.xml         | TDPRPS:t1:employee:t2  --> PPS:t1:employee |
| MT-RBACTIPRPSt1.xml         | TIPRPS:t1 --> PPS:t1:employee |
| MT-RBACTPSt1.xml `FIXME`    | TPS:t1:manager:t2 --> PPS:t1:manager <br/> TPS:t1:employee:t2 --> PPS:t1:employee <br/> TPS:t1:resource:t2:manager --> PPS:t1:manager |
| RBAC002PolicySetPPSemployee.xml | PPS:employee --> [create] purchase order |
| RBAC002PolicySetPPSmanager.xml  | PPS:manager --> [sign] purchase order <br/> PPS:manager --> PPS:employee |
| RBAC002Policy.xml           | RPS:manager --> PPS:manager<br/> RPS:employee --> PPS:employee |
| RBAC002Request.xml          | Anne (t2:manager) --> [create] purchase order in t1 |
| RBAC002Response.xml         | Permit |
