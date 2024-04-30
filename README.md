## Module Graph

```mermaid
graph BT;
subgraph core_data
core-data
end
subgraph feature_data
group-data:group1-data
group-data:group2-data
end
subgraph feature_common
feature-common
end
subgraph feature
feature:pluu
feature:group1:feature-a
feature:group1:feature-b
feature:group2-single
end

common--impl-->app;
common--impl-->core-data;
common--impl-->feature-common;
common--impl-->feature:pluu;
common--impl-->group-data:group1-data;
common--impl-->group-common:group1;
common--impl-->feature:group1:feature-a;
common--impl-->feature:group1:feature-b;
common--impl-->group-data:group2-data;
common--impl-->feature:group2-single;

core-data--impl-->app;
core-data--impl-->feature-common;
core-data--impl-->feature:pluu;
core-data--api-->group-data:group1-data;
core-data--api-->group-data:group2-data;
core-data--impl-->feature:group2-single;

feature-common--impl-->feature:pluu;        
feature-common--api-->group-common:group1;
feature-common--impl-->feature:group1:feature-a;
feature-common--impl-->feature:group1:feature-b;
feature-common--impl-->feature:group2-single;   

group-data:group1-data--impl-->group-common:group1;
group-data:group1-data--impl-->feature:group1:feature-a;
group-data:group1-data--impl-->feature:group1:feature-b;

group-common:group1--impl-->feature:group1:feature-a;
group-common:group1--impl-->feature:group1:feature-b;      

feature:pluu--impl-->app;     
group-data:group2-data--impl-->feature:group2-single;           
feature:group2-single--impl-->app;
feature:group1:feature-a--impl-->app;
feature:group1:feature-b--impl-->app;   

fake-lint-.->app;
fake-lint-.->core-data;
fake-lint-.->feature-common;
fake-lint-.->feature:pluu;
fake-lint-.->group-data:group1-data;
fake-lint-.->group-common:group1;
fake-lint-.->feature:group1:feature-a;
fake-lint-.->feature:group1:feature-b;
fake-lint-.->group-data:group2-data;
fake-lint-.->feature:group2-single;
      
linkStyle 13 stroke-width:4px,stroke:red
linkStyle 14 stroke-width:4px,stroke:red
linkStyle 17 stroke-width:4px,stroke:red
```

## Result

|        change :common        |      change :core-data       |
| :--------------------------: | :--------------------------: |
| <img src="arts/Case1.png" /> | <img src="arts/Case2.png" /> |

|    change :feature-common    |     change :feature:pluu     |
| :--------------------------: | :--------------------------: |
| <img src="arts/Case3.png" /> | <img src="arts/Case4.png" /> |

| change :group-data:group1-data and :feature:group1:feature-b |
| :----------------------------------------------------------: |
|            <img src="arts/Case5-multiple.png" />             |

