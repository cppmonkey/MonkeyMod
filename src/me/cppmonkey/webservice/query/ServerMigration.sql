insert into `mc_permissions`
(`player_id`, `server_id`, `permission`, `value`)
select `player_id`, 202 `server_id`, `permission`, `value`
from `mc_permissions`
where server_id = 2