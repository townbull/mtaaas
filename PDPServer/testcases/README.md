Test Cases
=========================

| File								| Content Description			|
|-----------------------------------|-------------------------------|
| MT-RBAC001Policy.xml 				| RPS:t1:manager --> TPS:t1<br/> RPS:t1:employee --> TPS:t1 	|
| MT-RBAC001PPSt1employee.xml 		| PPS:t1:employee --> [create] purchase order |
| MT-RBAC001PPSt1manager.xml 		| PPS:t1:manager --> [sign] purchase order <br/> PPS:t1:manager --> PPS:t1:employee |
| MT-RBAC001PPSt2employee.xml 		| PPS:t2:employee --> [create] purchase order |
| MT-RBAC001PPSt2manager.xml 		| PPS:t2:manager --> [sign] purchase order <br/> PPS:t2:manager --> PPS:t2:employee |
| MT-RBAC001Request.xml 			| Anne (t1:manager) --> [create] purchase order in t1 |
| MT-RBAC001Response.xml 			| Permit |
| MT-RBAC001TPSt1.xml 				| TPS:t2 or TPS:t1:manager --> PPS:t1:manager |
| MT-RBAC001TPSt2.xml 				| TPS:t1:manager --> PPS:t1:manager<br/> TPS:t2:manager --> PPS:t2:manager<br/> TPS:t2:employee --> PPS:t2:employee
| MT-RBAC002Policy.xml
| MT-RBAC002PPSt1employee.xml
| MT-RBAC002PPSt1manager.xml
| MT-RBAC002PPSt2employee.xml
| MT-RBAC002PPSt2manager.xml
| MT-RBAC002Request.xml
| MT-RBAC002Response.xml
| MT-RBAC002TPSt1.xml
| MT-RBAC003Policy.xml
| MT-RBAC003PPSt1employee.xml
| MT-RBAC003PPSt1manager.xml
| MT-RBAC003PPSt2employee.xml
| MT-RBAC003PPSt2manager.xml
| MT-RBAC003Request.xml
| MT-RBAC003Response.xml
| MT-RBAC003TIPRPSt1.xml
| MT-RBAC003TPSt1.xml
| MT-RBAC004Policy.xml
| MT-RBAC004PPSt1employee.xml
| MT-RBAC004PPSt1manager.xml
| MT-RBAC004PPSt2employee.xml
| MT-RBAC004PPSt2manager.xml
| MT-RBAC004Request.xml
| MT-RBAC004Response.xml
| MT-RBAC004TDPRPSt1.xml
| MT-RBAC004TPSt1.xml
| MT-RBACPolicy.xml
| MT-RBACPPSt1employee.xml
| MT-RBACPPSt1manager.xml
| MT-RBACRequest.xml
| MT-RBACResponse.xml
| MT-RBACTDPRPSt1.xml
| MT-RBACTIPRPSt1.xml
| MT-RBACTPSt1.xml
| RBAC002PolicySetPPSemployee.xml
| RBAC002PolicySetPPSmanager.xml
| RBAC002Policy.xml
| RBAC002Request.xml
| RBAC002Response.xml
