package map;

import java.io.Serializable;

public class HexData implements Serializable {
    boolean traversable;
    int terraincost;

    Map.TerrainType type;
    Map.Modifier modifier;

    public HexData() {
        type = Map.TerrainType.Ocean;

        this.traversable = false;
        this.terraincost = 1;
    }

    public void setTerrain(Map.TerrainType type){
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

    public void setModifier(Map.Modifier modifier){
        this.modifier = modifier;
    }
}
