# Membership StudEV Services

## Unenroll from group

**Name of service:** `UnenrollFromGroup`

**SQL query:**

```sql
DELETE gm, r
FROM GroupMembership gm
LEFT JOIN `Request` r
  ON r.user_id = gm.user_id
 AND r.group_id = gm.group_id
 AND r.description = 'requested_user_enrollment'
WHERE gm.membership_id = (:membershipid);
```

**StudEV path order for Android:**

```text
UnenrollFromGroup/{membershipid}
```
