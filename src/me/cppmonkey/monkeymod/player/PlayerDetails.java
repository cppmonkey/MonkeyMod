p%ackage me.cppmonkey.monkeymod.player;


public class PlayerDetails {

    public PlayerDetails(Integer playerUID){
        this.m_playerUID = playerUID;
    }

    private Integer m_playerUID;

    public Integer getPlayerUID(){return m_playerUID;}

    private Boolean m_canBuild;

    public void setCanBuild(Boolean canBuild){ this.m_canBuild = canBuild; }
    public Boolean canBuild(){ return m_canBuild; }

    private Boolean m_canIgnite;

    public void setCanIgnite(Boolean canIgnite){ this.m_canIgnite = canIgnite; }
    public Boolean canIgnite(){ return m_canIgnite; }

    /*
    private Boolean canSpawn;

    public void setCanBuild(Boolean canBuild){ this.canBuild = canBuild; }
    public Boolean getCanBuild(){ return canBuild; }

    */

    private Boolean m_isVip;

    public void setIsVip(Boolean isVip){ this.m_isVip = isVip; }
    public Boolean isVip(){ return m_isVip; }

    private Boolean m_isAdmin;

    public void setIsAdmin(Boolean isAdmin){ this.m_isAdmin = isAdmin; }
    public Boolean isAdmin(){ return m_isAdmin; }
}
