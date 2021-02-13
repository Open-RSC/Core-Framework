/**
*  Trains agility at the gnome agility arena. start anywhere
*  along the agility route to begin.
*
*   v1.1
*
*   - yomama`
*/
public class GnomeAgility extends Script {
 
  int fmode = 1;
 
  public GnomeAgility(Extension e)  {
  super(e);
  }
 
  public void init(String params){
     if(!params.equals(""))
     fmode = Integer.parseInt(params);
  }
 
  public int main(){
     int[] obj = new int[]{-1,-1,-1};
     
     if(getFightMode() != fmode)
        setFightMode(fmode);
     if(getFatigue() > 90) {
        useSleepingBag();
        return 1000;
     } else if(getY() > 2393)
        if(getX() > 688)
           obj = getObjectById(650); // rope
        else
           obj = getObjectById(649); // down tower
     else if(getY() > 1447)
        obj = getObjectById(648); // uptower
     else if(getY() < 496 || (getX() ==692 && getY() == 498))
        obj = getObjectById(655); // log
     else if(getX() > 688)
           obj = getObjectById(647); // up net
     else if(getY() < 502)  
        obj = getObjectById(654); // pipe
     else
        obj = getObjectById(653); // pipe
     
     if(obj[0] != -1)
        atObject(obj[1], obj[2]);
 
     return random(500,1000);
  }
}
