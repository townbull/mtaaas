Test Cases
=========================

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
