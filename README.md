```mermaid
graph BT;
common--impl-->app;
common--impl-->core-data;
common--impl-->feature-common;
common--impl-->pluu;
common--impl-->group1-data;
common--impl-->group1-common;
common--impl-->group1:feature-a;
common--impl-->group1:feature-b;
common--impl-->group2-data;
common--impl-->group2-single;

core-data--impl-->app;
core-data--impl-->feature-common;
core-data--impl-->pluu;
core-data--api-->group1-data;
core-data--impl-->group1-common;
core-data--api-->group2-data;
core-data--impl-->group2-single;

feature-common--impl-->pluu;        
feature-common--api-->group1-common;
feature-common--impl-->group1:feature-a;
feature-common--impl-->group1:feature-b;
feature-common--impl-->group2-single;   

group1-data--impl-->group1-common;
group1-data--impl-->group1:feature-a;
group1-data--impl-->group1:feature-b;

group1-common--impl-->group1:feature-a;
group1-common--impl-->group1:feature-b;      

pluu--impl-->app;     
group2-data--impl-->group2-single;           
group2-single--impl-->app;
group1:feature-a--impl-->app;
group1:feature-b--impl-->app;     
      
linkStyle 13 stroke-width:4px,stroke:red
linkStyle 15 stroke-width:4px,stroke:red
linkStyle 18 stroke-width:4px,stroke:red
```
