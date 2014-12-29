package me.cppmonkey.monkeymod.player;

import java.sql.Timestamp;
import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.Material;


public class PlayerDetails {

    private Integer m_playerUID;

    private Integer m_blocksPlaced = 0;
    private Integer m_blocksIgnited = 0;
    private Integer m_blocksDestroyed = 0;
    private Integer m_deniedBlocksPlaced = 0;
    private Integer m_deniedBlocksDamaged = 0;
    private Integer m_deniedBlocksIgnited = 0;
    private Integer m_deniedBlocksDestroyed = 0;

    private Boolean m_canBuild = false;
    private Boolean m_canIgnite = false;
    private Boolean m_isVip = false;
    private Boolean m_isAdmin = false;
    private Timestamp m_connectionTime;

    private HashMap<Material, Boolean> m_canSpawnList = new HashMap<Material, Boolean>();
    private HashMap<Material, Boolean> m_cantPlaceList = new HashMap<Material, Boolean>();

    public PlayerDetails(Integer playerUID){
        this.m_playerUID = playerUID;
        java.util.Date date= new java.util.Date();
        m_connectionTime = new Timestamp(date.getTime());
    }

    public PlayerDetails(Integer playerUID, GameMode gameMode){
        this.m_playerUID = playerUID;
        java.util.Date date= new java.util.Date();
        m_connectionTime = new Timestamp(date.getTime());

        if( gameMode == GameMode.CREATIVE ) {
            m_canBuild = true;
            m_canIgnite = true;
            m_isVip = true;
            m_isAdmin = true;
        }
    }

    // Getters
    public Integer playerUID(){return m_playerUID;}

    public Integer blocksPlaced(){return m_blocksPlaced;}
    public Integer blocksIgnited(){return m_blocksIgnited;}
    public Integer blocksDestroyed(){return m_blocksDestroyed;}

    public Integer deniedBlocksPlaced() { return m_deniedBlocksPlaced;}
    public Integer deniedBlocksDamaged() { return m_deniedBlocksDamaged;}
    public Integer deniedBlocksIgnited() { return m_deniedBlocksIgnited;}
    public Integer deniedBlocksDestroyed() { return m_deniedBlocksDestroyed;}

    public Boolean canBuild(){ return m_canBuild; }
    public Boolean canIgnite(){ return m_canIgnite; }
    public Boolean isVip(){ return m_isVip; }
    public Boolean isAdmin(){ return m_isAdmin; }
    public Timestamp connectionTime(){ return m_connectionTime; }

    // Setters
    public void setCanIgnite(Boolean canIgnite){ this.m_canIgnite = canIgnite; }
    public void setCanBuild(Boolean canBuild){ this.m_canBuild = canBuild; }
    public void setIsVip(Boolean isVip){ this.m_isVip = isVip; }
    public void setIsAdmin(Boolean isAdmin){ this.m_isAdmin = isAdmin; }
    public void setCantPlace(HashMap<Material, Boolean> CantPlaceList){ this.m_cantPlaceList = CantPlaceList; }
    public void setCanSpawn(HashMap<Material, Boolean> canSpawnList){ this.m_canSpawnList = canSpawnList; }

    // Incrementers
    public void incrementBlocksPlaced() {m_blocksPlaced++;}
    public void incrementBlocksIgnited() {m_blocksIgnited++;}
    public void incrementBlocksDestroyed() {m_blocksDestroyed++;}
    public void incrementDeniedBlocksPlaced() {m_deniedBlocksPlaced++;}
    public void incrementDeniedBlocksDamaged() {m_deniedBlocksDamaged++;}
    public void incrementDeniedBlocksIgnited() {m_deniedBlocksIgnited++;}
    public void incrementDeniedBlocksDestroyed() {m_deniedBlocksDestroyed++;}

    // Reset Counters
    public void resetAllCounters() {
        resetBlocksPlaced();
        resetBlocksIgnited();
        resetBlocksDestroyed();
        resetDeniedBlocksPlaced();
        resetDeniedBlocksDamaged();
        resetDeniedBlocksIgnited();
        resetDeniedBlocksDestroyed();
    }
    public void resetBlocksPlaced() { m_blocksPlaced = 0; }
    public void resetBlocksIgnited() { m_blocksIgnited = 0; }
    public void resetBlocksDestroyed() { m_blocksDestroyed = 0; }
    public void resetDeniedBlocksPlaced() { m_deniedBlocksPlaced = 0; };
    public void resetDeniedBlocksDamaged() { m_deniedBlocksDamaged = 0; }
    public void resetDeniedBlocksIgnited() { m_deniedBlocksIgnited = 0; }
    public void resetDeniedBlocksDestroyed() { m_deniedBlocksDestroyed = 0; }

    // TODO should this be can't spawn? Inclusive rather than exclusion

    public Boolean getCanSpawn(Material material){ return getCanSpawn(material, false);}
    public Boolean getCanSpawn(Material material, Boolean defaultResponce){
        if(m_canSpawnList.containsKey(material)){
            return m_canSpawnList.get(material);
        }
        return defaultResponce;
    }

    public Boolean cantPlace(Material material){ return cantPlace(material, false);}
    public Boolean cantPlace(Material material, Boolean response){
        if(m_cantPlaceList.containsKey(material)){
            return m_cantPlaceList.get(material);
        }
        return response;
    }
}
