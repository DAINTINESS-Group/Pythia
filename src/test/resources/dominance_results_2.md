Dominance Pattern Highlights With Two Coordinate Columns

- Measurement Column Name: download (Aggregate)
- Fist Coordinate Column Name: name (Group By)
- Second Coordinate Column Name: session_break_reason (Group By)

- Query Result:
user4	User-Request	7751.68	
user2	Lost-Carrier	383324.16	
user1	Idle-Timeout	70335.32631578947	
user5	Idle-Timeout	592516.5511111112	
user3	Idle-Timeout	319128.67687499995	
user5	Lost-Carrier	66119.68	
user2	Idle-Timeout	552828.9105555554	
user3	Lost-Carrier	120637.44	
user5	User-Request	831201.28	
user1	Lost-Carrier	8110.08	
user4	Idle-Timeout	295296.8084210526	
user2	Lost-Service	190535.68	

- Identified Highlights:
Coordinates: user4, User-Request have an aggregate (avg) value of 7751.68 
and a total low dominance of 100.0% over the other aggregate values of the query results.

Coordinates: user5, Idle-Timeout have an aggregate (avg) value of 592516.5511111112 
and a partial high dominance of 90.9090909090909% over the other aggregate values of the query results.

Coordinates: user5, Lost-Carrier have an aggregate (avg) value of 66119.68 
and a partial low dominance of 81.81818181818183% over the other aggregate values of the query results.

Coordinates: user2, Idle-Timeout have an aggregate (avg) value of 552828.9105555554 
and a partial high dominance of 81.81818181818183% over the other aggregate values of the query results.

Coordinates: user5, User-Request have an aggregate (avg) value of 831201.28 
and a total high dominance of 100.0% over the other aggregate values of the query results.

Coordinates: user1, Lost-Carrier have an aggregate (avg) value of 8110.08 
and a partial low dominance of 90.9090909090909% over the other aggregate values of the query results.

