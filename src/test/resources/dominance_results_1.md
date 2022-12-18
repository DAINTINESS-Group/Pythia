## Dominance Pattern Results With One Coordinate Column

- Aggregation  Method: avg
- Measurement Column Name: mileage
- Coordinate X Column Name: model

### Detailed Results:
model    mileage (avg)    High%    Low%    Is highlight?    Highlight Type    
A1       27769.058        25.0     75.0    true             partial low       
A3       31739.59         50.0     50.0    false            -                 
A4       31529.538        37.5     62.5    false            -                 
A5       34838.333        62.5     37.5    false            -                 
A6       40924.5          87.5     12.5    true             partial high      
Q2       6080.5           0.0      100.0   true             total low         
Q3       35638.0          75.0     25.0    true             partial high      
Q5       40967.8          100.0    0.0     true             total high        
S4       20278.0          12.5     87.5    true             partial low       

### Identified Highlights:
- Coordinate: A1 (model) has an aggregate (avg) value of 27769.058 (mileage)
and a partial low dominance of 75% over the other aggregate values of the query results.

- Coordinate: A6 (model) has an aggregate (avg) value of 40924.5 (mileage)
and a partial high dominance of 87.5% over the other aggregate values of the query results.

- Coordinate: Q2 (model) has an aggregate (avg) value of 6080.5 (mileage)
and a total low dominance of 100% over the other aggregate values of the query results.

- Coordinate: Q3 (model) has an aggregate (avg) value of 35638 (mileage)
and a partial high dominance of 75% over the other aggregate values of the query results.

- Coordinate: Q5 (model) has an aggregate (avg) value of 40967.8 (mileage)
and a total high dominance of 100% over the other aggregate values of the query results.

- Coordinate: S4 (model) has an aggregate (avg) value of 20278 (mileage)
and a partial low dominance of 87.5% over the other aggregate values of the query results.

