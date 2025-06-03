## How to create cron job in Springboot
1. Structure of Cron in Spring

The cron used in Spring framework has the format
```java
second minute hour day-of-month month day-of-week year (optional)
```
Example

| Expression      | Meaning                               |
| --------------- | ------------------------------------- |
| `0 0 1 * * ?`   | Every day at 1:00 AM                  |
| `0 0/5 * * * ?` | Every 5 minutes                       |
| `0 0 0 * * MON` | Every Monday at midnight              |
| `0 0 12 1 * ?`  | On the 1st day of every month at noon |

The cron is used default server timezone. If a specific timezone is needed, use

```java
@Scheduled(cron = "0 0 9 * * *", zone = "Asia/Kolkata")
```

We can also fix rate for scheduled tasks
```java
@Scheduled(fixedRate = 10000)  // every 10 seconds
public void periodicTask() {
    System.out.println("Runs every 10 seconds.");
}

```
2. Field meaning in cron job

| Field            | Allowed Values      | Special Characters |
| ---------------- | ------------------- | ------------------ |
| **Seconds**      | `0–59`              | `, - * /`          |
| **Minutes**      | `0–59`              | `, - * /`          |
| **Hours**        | `0–23`              | `, - * /`          |
| **Day of Month** | `1–31`              | `, - * ? / L W`    |
| **Month**        | `1–12` or `JAN–DEC` | `, - * /`          |
| **Day of Week**  | `0–6` or `SUN–SAT`  | `, - * ? / L #`    |

`*` means ``every`` and `?` means `no specific value` (used when one of day of week or day of month is specified)

3. Special charactor in cron

| Character | Meaning                          | Example                                       | Description                                      |
| --------- | -------------------------------- | --------------------------------------------- | ------------------------------------------------ |
| `*`       | **All values**                   | `*` in the minute field = every minute        | Wildcard for “every possible value” of a field   |
| `?`       | **No specific value**            | Used in day-of-month or day-of-week           | Prevents conflict when both DOM and DOW are used |
| `-`       | **Range**                        | `10-12` = 10, 11, 12                          | Specifies a range of values                      |
| `,`       | **List**                         | `MON,WED,FRI`                                 | Specifies multiple values                        |
| `/`       | **Step**                         | `0/15` in minute = every 15 min starting at 0 | Specifies increments                             |
| `L`       | **Last**                         | `L` in DOW = last day of week (Saturday)      | Means “last” in context                          |
| `W`       | **Weekday nearest to given day** | `15W` = nearest weekday to 15th               | Used only in day-of-month                        |
| `#`       | **Nth occurrence of weekday**    | `3#2` = 2nd Tuesday (3 = Tuesday)             | Only for day-of-week field                       |

Example 
```
0 30 10 15W * ?     → 10:30 AM on the nearest weekday to the 15th of every month
0 0 12 ? * MON-FRI  → Every weekday at 12 PM
0 0 18 L * ?        → 6 PM on the last day of every month
0 0 8 ? * 2#1       → 8 AM on the first Monday of every month
```

4. Some advanced cron job
- Quartz class for setting cron job