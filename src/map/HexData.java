package map;

import java.io.Serializable;

public class HexData implements Serializable {
    Terrain terrain;
    State state;
    Building building;

    public void setTerrain(Terrain terrain){
        if(this.terrain == terrain){
            this.terrain = null;
        }
        else{
            this.terrain = terrain;
        }
    }

    public void setState(State state){
        if(this.state == state){
            this.state = null;
        }
        else{
            this.state = state;
        }
    }

    public void setBuilding(Building building) {
        if(this.building == building){
            this.building = null;
        }
        else{
            this.building = building;
        }
    }

    @Override
    public String toString() {
        return "HexData{" +
                "terrain=" + terrain +
                ", state=" + state +
                ", building=" + building +
                '}';
    }
}
