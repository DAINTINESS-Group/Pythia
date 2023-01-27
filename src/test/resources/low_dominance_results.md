## Low Dominance Pattern Results


----------------------------------------------------------------------------------------------------


### Metadata:
- Dominance Type:             low dominance
- Num. of Coordinates:        1
- Aggregation Method:         sum
- Measurement Column Name:    price
- Coordinate X Column Name:   model

### Detailed Results:
model   price (sum)   Dominance%   Is highlight?   Highlight Type   
A4      6500.0        100.0        true            total low        
S4      23700.0       87.5         true            partial low      
A6      40465.0       75.0         true            partial low      
Q2      56499.0       62.5         false           -                
A5      94570.0       50.0         false           -                
A1      197000.0      37.5         false           -                

### Identified Highlights:
- Coordinate: A4 (model) has an aggregate (sum) value of 6500 (price)
and a total low dominance of 100% over the aggregate values of the other models.

- Coordinate: S4 (model) has an aggregate (sum) value of 23700 (price)
and a partial low dominance of 87.5% over the aggregate values of the other models.

- Coordinate: A6 (model) has an aggregate (sum) value of 40465 (price)
and a partial low dominance of 75% over the aggregate values of the other models.


----------------------------------------------------------------------------------------------------


### Metadata:
- Dominance Type:             low dominance
- Num. of Coordinates:        1
- Aggregation Method:         sum
- Measurement Column Name:    price
- Coordinate X Column Name:   year

### Detailed Results:
year   price (sum)   Dominance%   Is highlight?   Highlight Type   
2013   18500.0       100.0        true            total low        
2018   207960.0      83.333       true            partial low      
2019   1201298.0     66.666       false           -                
2015   2119590.0     50.0         false           -                
2014   2917500.0     33.333       false           -                
2017   3311070.0     16.666       false           -                

### Identified Highlights:
- Coordinate: 2013 (year) has an aggregate (sum) value of 18500 (price)
and a total low dominance of 100% over the aggregate values of the other years.

- Coordinate: 2018 (year) has an aggregate (sum) value of 207960 (price)
and a partial low dominance of 83.333% over the aggregate values of the other years.


----------------------------------------------------------------------------------------------------


### Metadata:
- Dominance Type:             low dominance
- Num. of Coordinates:        2
- Aggregation Method:         sum
- Measurement Column Name:    price
- Coordinate X Column Name:   model
- Coordinate Y Column Name:   year

### Detailed Results:
model   Dominates the model(s)             for the year(s)                        Dominance%   Is highlight?   Highlight Type   Aggr. Marginal Sum (price)   
A4      [A1, A3, A5, A6, Q2, Q3, Q5, S4]   [2016, 2017, 2018, 2019]               100.0        true            total low        6500.0                       
A6      [A1, A3, A5, Q3, Q5, S4]           [2015, 2016, 2017, 2018]               75.0         true            partial low      40465.0                      
S4      [A1, A3, A5, Q3]                   [2017]                                 50.0         false           -                23700.0                      
A1      [A3, Q3, Q5]                       [2013, 2014, 2015, 2016, 2017, 2018]   37.5         false           -                197000.0                     
A3      [Q3, Q5]                           [2014, 2015, 2016, 2017, 2019]         25.0         false           -                1897299.0                    
A5      [A3, Q3]                           [2014, 2017]                           25.0         false           -                94570.0                      

### Identified Highlights:
- Coordinate X: A4 (model) presents a total low dominance over the other models of the dataset.
In detail, the aggregate values of A4 for the years (Y coordinate): [2016, 2017, 2018, 2019]
dominate the aggregate values of the models: [A1, A3, A5, A6, Q2, Q3, Q5, S4]
Overall, A4 has a dominance percentage score of 100% and an aggregate marginal sum of 6500 (price).

- Coordinate X: A6 (model) presents a partial low dominance over the other models of the dataset.
In detail, the aggregate values of A6 for the years (Y coordinate): [2015, 2016, 2017, 2018]
dominate the aggregate values of the models: [A1, A3, A5, Q3, Q5, S4]
Overall, A6 has a dominance percentage score of 75% and an aggregate marginal sum of 40465 (price).


----------------------------------------------------------------------------------------------------


### Metadata:
- Dominance Type:             low dominance
- Num. of Coordinates:        2
- Aggregation Method:         sum
- Measurement Column Name:    price
- Coordinate X Column Name:   year
- Coordinate Y Column Name:   model

### Detailed Results:
year   Dominates the year(s)                  for the model(s)   Dominance%   Is highlight?   Highlight Type   Aggr. Marginal Sum (price)   
2013   [2014, 2015, 2016, 2017, 2018, 2019]   [A1, A3]           100.0        true            total low        18500.0                      
2018   [2016, 2017]                           [A1, A3, A4, A6]   33.333       false           -                207960.0                     
2019   [2016, 2017]                           [A3, A4, Q3]       33.333       false           -                1201298.0                    
2014   [2016]                                 [A1, A3, Q3, A5]   16.666       false           -                2917500.0                    
2015   [2017]                                 [A1, A3, A6, Q3]   16.666       false           -                2119590.0                    
2016   []                                     []                 0.0          false           -                5362045.0                    

### Identified Highlights:
- Coordinate X: 2013 (year) presents a total low dominance over the other years of the dataset.
In detail, the aggregate values of 2013 for the models (Y coordinate): [A1, A3]
dominate the aggregate values of the years: [2014, 2015, 2016, 2017, 2018, 2019]
Overall, 2013 has a dominance percentage score of 100% and an aggregate marginal sum of 18500 (price).
