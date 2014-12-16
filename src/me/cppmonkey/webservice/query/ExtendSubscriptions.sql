# Extend subscription due to inability to use

# Adds 7 days to subscriptions
UPDATE `mc_subscription`
SET `startdate` = startdate + INTERVAL 7 DAY