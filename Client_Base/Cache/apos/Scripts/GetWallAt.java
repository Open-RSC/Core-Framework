public final class GetWallAt extends Script {

    public GetWallAt(Extension ex) {
        super(ex);
    }
    
    @Override
    public void init(String params) {
        String[] split = params.split(",");
        System.out.println(getWallObjectIdFromCoords(
                Integer.parseInt(split[0]),
                Integer.parseInt(split[1])));
    }
}
