step-1 : clone the repo:


step-2 : have mysq DB and git bash installed locally and ofc jdk and javac too 

step-3 : in git bash in the root run

```
bash build.sh
``` 


step-4 : now run it

```
 ./run.sh
```

 
### MAKE SURE TO ADD YOUR OWN DB CONGIG.

```
package com.nabin.taskmanager.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/yourowndbname";
    private static final String USER = "root";
    private static final String PASSWORD = "yourowndbpassword";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
```


### PREVIEW:


![Screenshot from 2025-05-17 13-31-28](https://github.com/user-attachments/assets/66af8a27-a370-44ee-a46a-8a840075999a)

