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

![image](https://github.com/user-attachments/assets/74c1d1da-75b6-4f2e-87b5-f0c1b5747d7e)

