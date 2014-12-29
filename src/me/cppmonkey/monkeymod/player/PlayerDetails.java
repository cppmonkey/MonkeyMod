package me.cppmonkey.monkeymod.player;

import java.util.HashMap;

import org.bukkit.Material;


public class PlayerDetails {

    private Integer m_playerUID;
    private Boolean m_canBuild = false;
    private Boolean m_canIgnite = false;
    private Boolean m_isVip = false;
    private Boolean m_isAdmin = false;

    private HashMap<Material, Boolean> m_canSpawnList;
    private HashMap<Material, Boolean> m_cantPlaceList;

    public PlayerDetails(Integer playerUID){
        this.m_playerUID = playerUID;
    }

    // Getters
    public Integer playerUID(){return m_playerUID;}

    public Boolean canBuild(){ return m_canBuild; }
    public Boolean canIgnite(){ return m_canIgnite; }
    public Boolean isVip(){ return m_isVip; }
    public Boolean isAdmin(){ return m_isAdmin; }

    // Setters
    public void setCanIgnite(Boolean canIgnite){ this.m_canIgnite = canIgnite; }
    public void setCanBuild(Boolean canBuild){ this.m_canBuild = canBuild; }
    public void setIsVip(Boolean isVip){ this.m_isVip = isVip; }
    public void setIsAdmin(Boolean isAdmin){ this.m_isAdmin = isAdmin; }
    public void setCantPlace(HashMap<Material, Boolean> CantPlaceList){ this.m_cantPlaceList = CantPlaceList; }
    public void setCanSpawn(HashMap<Material, Boolean> canSpawnList){ this.m_canSpawnList = canSpawnList; }

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
