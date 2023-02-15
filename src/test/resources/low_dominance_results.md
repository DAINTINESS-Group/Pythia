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
model   Dominates the model(s)             Dominance%   Is highlight?   Highlight Type   Aggr. Marginal Sum (price)   
A4      [A1, A3, A5, A6, Q2, Q3, Q5, S4]   100.0        true            total low        6500.0                       
A6      [A1, A3, A5, Q3, Q5, S4]           75.0         true            partial low      40465.0                      
S4      [A1, A3, A5, Q3]                   50.0         false           -                23700.0                      
A1      [A3, Q3, Q5]                       37.5         false           -                197000.0                     
A3      [Q3, Q5]                           25.0         false           -                1897299.0                    
A5      [A3, Q3]                           25.0         false           -                94570.0                      

### Identified Highlights:
- Coordinate X: A4 (model) presents a total low dominance over the models: [A1, A3, A5, A6, Q2, Q3, Q5, S4].
In detail, the aggregate values of A4 dominate the models:
A1 on the year(s): [2016, 2017, 2018].
A3 on the year(s): [2016, 2017, 2018, 2019].
A5 on the year(s): [2017].
A6 on the year(s): [2016, 2017, 2018].
Q2 on the year(s): [2019].
Q3 on the year(s): [2016, 2017, 2019].
Q5 on the year(s): [2016].
S4 on the year(s): [2017].
Overall, A4 has a dominance percentage score of 100% and an aggregate marginal sum of 6500 (price).

- Coordinate X: A6 (model) presents a partial low dominance over the models: [A1, A3, A5, Q3, Q5, S4].
In detail, the aggregate values of A6 dominate the models:
A1 on the year(s): [2015, 2016, 2017, 2018].
A3 on the year(s): [2015, 2016, 2017, 2018].
A5 on the year(s): [2017].
Q3 on the year(s): [2015, 2016, 2017].
Q5 on the year(s): [2015, 2016].
S4 on the year(s): [2017].
Overall, A6 has a dominance percentage score of 75% and an aggregate marginal sum of 40465 (price).

### Query Results:
model     year      price     
A1        2013      7000.0    
A1        2014      21000.0   
A1        2015      9500.0    
A1        2016      83400.0   
A1        2017      60300.0   
A1        2018      15800.0   
A3        2013      11500.0   
A3        2014      139000.0  
A3        2015      273200.0  
A3        2016      254200.0  
A3        2017      814100.0  
A3        2018      189500.0  
A3        2019      215799.0  
A4        2016      1500.0    
A4        2017      3000.0    
A4        2018      1000.0    
A4        2019      1000.0    
A5        2014      13200.0   
A5        2017      81370.0   
A6        2015      5990.0    
A6        2016      16615.0   
A6        2017      16200.0   
A6        2018      1660.0    
Q2        2019      56499.0   
Q3        2014      2744300.0 
Q3        2015      916200.0  
Q3        2016      3055710.0 
Q3        2017      2312400.0 
Q3        2019      928000.0  
Q5        2015      914700.0  
Q5        2016      1950620.0 
S4        2017      23700.0   


----------------------------------------------------------------------------------------------------


### Metadata:
- Dominance Type:             low dominance
- Num. of Coordinates:        2
- Aggregation Method:         sum
- Measurement Column Name:    price
- Coordinate X Column Name:   year
- Coordinate Y Column Name:   model

### Detailed Results:
year   Dominates the year(s)                  Dominance%   Is highlight?   Highlight Type   Aggr. Marginal Sum (price)   
2013   [2014, 2015, 2016, 2017, 2018, 2019]   100.0        true            total low        18500.0                      
2018   [2016, 2017]                           33.333       false           -                207960.0                     
2019   [2016, 2017]                           33.333       false           -                1201298.0                    
2014   [2016]                                 16.666       false           -                2917500.0                    
2015   [2017]                                 16.666       false           -                2119590.0                    
2016   []                                     0.0          false           -                5362045.0                    

### Identified Highlights:
- Coordinate X: 2013 (year) presents a total low dominance over the years: [2014, 2015, 2016, 2017, 2018, 2019].
In detail, the aggregate values of 2013 dominate the years:
2014 on the model(s): [A1, A3].
2015 on the model(s): [A1, A3].
2016 on the model(s): [A1, A3].
2017 on the model(s): [A1, A3].
2018 on the model(s): [A1, A3].
2019 on the model(s): [A3].
Overall, 2013 has a dominance percentage score of 100% and an aggregate marginal sum of 18500 (price).

### Query Results:
year      model     price     
2013      A1        7000.0    
2013      A3        11500.0   
2014      A1        21000.0   
2014      A3        139000.0  
2014      A5        13200.0   
2014      Q3        2744300.0 
2015      A1        9500.0    
2015      A3        273200.0  
2015      A6        5990.0    
2015      Q3        916200.0  
2015      Q5        914700.0  
2016      A1        83400.0   
2016      A3        254200.0  
2016      A4        1500.0    
2016      A6        16615.0   
2016      Q3        3055710.0 
2016      Q5        1950620.0 
2017      A1        60300.0   
2017      A3        814100.0  
2017      A4        3000.0    
2017      A5        81370.0   
2017      A6        16200.0   
2017      Q3        2312400.0 
2017      S4        23700.0   
2018      A1        15800.0   
2018      A3        189500.0  
2018      A4        1000.0    
2018      A6        1660.0    
2019      A3        215799.0  
2019      A4        1000.0    
2019      Q2        56499.0   
2019      Q3        928000.0  
