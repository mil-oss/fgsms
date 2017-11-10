# Important!

When regenerating the code from xsd/wsdl be careful what you commit. 
When running on the Apache CXF stack with schema validation turned on, the 
generated code (out of the box) will not validate correct when generated from
jdk/wsimport. In order to resolve this, many of the classes were hand 
modified in order to have the same functional meaning and pass schema validation.