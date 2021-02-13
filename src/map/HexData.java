package map;

import java.io.Serializable;

public class HexData implements Serializable {
    boolean traversable;
    int terraincost;
    enum TerrainType {Ocean, Land, Lake}
    TerrainType type;

    enum Modifier {Move}
    Modifier modifier;

    public HexData() {
        type = TerrainType.Ocean;

        this.traversable = false;
        this.terraincost = 1;
    }

    public void setTerrain(TerrainType type){
        switch (type){
            case Lake:
            case Ocean:
                this.type = type;
                traversable = false;
                break;
            case Land:
                this.type = type;
                traversable = true;
                break;
        }
    }

    public void setModifier(Modifier modifier){
        this.modifier = modifier;
    }
}
