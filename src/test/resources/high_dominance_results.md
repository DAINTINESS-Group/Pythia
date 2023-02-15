## High Dominance Pattern Results


----------------------------------------------------------------------------------------------------


### Metadata:
- Dominance Type:             high dominance
- Num. of Coordinates:        1
- Aggregation Method:         sum
- Measurement Column Name:    price
- Coordinate X Column Name:   model

### Detailed Results:
model   price (sum)   Dominance%   Is highlight?   Highlight Type   
Q3      9956610.0     100.0        true            total high       
Q5      2865320.0     87.5         true            partial high     
A3      1897299.0     75.0         true            partial high     
A1      197000.0      62.5         false           -                
A5      94570.0       50.0         false           -                
Q2      56499.0       37.5         false           -                

### Identified Highlights:
- Coordinate: Q3 (model) has an aggregate (sum) value of 9956610 (price)
and a total high dominance of 100% over the aggregate values of the other models.

- Coordinate: Q5 (model) has an aggregate (sum) value of 2865320 (price)
and a partial high dominance of 87.5% over the aggregate values of the other models.

- Coordinate: A3 (model) has an aggregate (sum) value of 1897299 (price)
and a partial high dominance of 75% over the aggregate values of the other models.


----------------------------------------------------------------------------------------------------


### Metadata:
- Dominance Type:             high dominance
- Num. of Coordinates:        1
- Aggregation Method:         sum
- Measurement Column Name:    price
- Coordinate X Column Name:   year

### Detailed Results:
year   price (sum)   Dominance%   Is highlight?   Highlight Type   
2016   5362045.0     100.0        true            total high       
2017   3311070.0     83.333       true            partial high     
2014   2917500.0     66.666       false           -                
2015   2119590.0     50.0         false           -                
2019   1201298.0     33.333       false           -                
2018   207960.0      16.666       false           -                

### Identified Highlights:
- Coordinate: 2016 (year) has an aggregate (sum) value of 5362045 (price)
and a total high dominance of 100% over the aggregate values of the other years.

- Coordinate: 2017 (year) has an aggregate (sum) value of 3311070 (price)
and a partial high dominance of 83.333% over the aggregate values of the other years.


----------------------------------------------------------------------------------------------------


### Metadata:
- Dominance Type:             high dominance
- Num. of Coordinates:        2
- Aggregation Method:         sum
- Measurement Column Name:    price
- Coordinate X Column Name:   model
- Coordinate Y Column Name:   year

### Detailed Results:
model   Dominates the model(s)             Dominance%   Is highlight?   Highlight Type   Aggr. Marginal Sum (price)   
Q3      [A1, A3, A4, A5, A6, Q2, Q5, S4]   100.0        true            total high       9956610.0                    
A3      [A1, A4, A5, A6, Q2, S4]           75.0         true            partial high     1897299.0                    
Q5      [A1, A3, A4, A6]                   50.0         false           -                2865320.0                    
A1      [A4, A6, S4]                       37.5         false           -                197000.0                     
A5      [A4, A6, S4]                       37.5         false           -                94570.0                      
S4      [A4, A6]                           25.0         false           -                23700.0                      

### Identified Highlights:
- Coordinate X: Q3 (model) presents a total high dominance over the models: [A1, A3, A4, A5, A6, Q2, Q5, S4].
In detail, the aggregate values of Q3 dominate the models:
A1 on the year(s): [2014, 2015, 2016, 2017].
A3 on the year(s): [2014, 2015, 2016, 2017, 2019].
A4 on the year(s): [2016, 2017, 2019].
A5 on the year(s): [2014, 2017].
A6 on the year(s): [2015, 2016, 2017].
Q2 on the year(s): [2019].
Q5 on the year(s): [2015, 2016].
S4 on the year(s): [2017].
Overall, Q3 has a dominance percentage score of 100% and an aggregate marginal sum of 9956610 (price).

- Coordinate X: A3 (model) presents a partial high dominance over the models: [A1, A4, A5, A6, Q2, S4].
In detail, the aggregate values of A3 dominate the models:
A1 on the year(s): [2013, 2014, 2015, 2016, 2017, 2018].
A4 on the year(s): [2016, 2017, 2018, 2019].
A5 on the year(s): [2014, 2017].
A6 on the year(s): [2015, 2016, 2017, 2018].
Q2 on the year(s): [2019].
S4 on the year(s): [2017].
Overall, A3 has a dominance percentage score of 75% and an aggregate marginal sum of 1897299 (price).

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
- Dominance Type:             high dominance
- Num. of Coordinates:        2
- Aggregation Method:         sum
- Measurement Column Name:    price
- Coordinate X Column Name:   year
- Coordinate Y Column Name:   model

### Detailed Results:
year   Dominates the year(s)      Dominance%   Is highlight?   Highlight Type   Aggr. Marginal Sum (price)   
2016   [2013, 2014, 2018, 2019]   66.666       false           -                5362045.0                    
2017   [2013, 2015, 2018, 2019]   66.666       false           -                3311070.0                    
2014   [2013]                     16.666       false           -                2917500.0                    
2015   [2013]                     16.666       false           -                2119590.0                    
2018   [2013]                     16.666       false           -                207960.0                     
2019   [2013]                     16.666       false           -                1201298.0                    

### Identified Highlights:
No highlights identified.

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
