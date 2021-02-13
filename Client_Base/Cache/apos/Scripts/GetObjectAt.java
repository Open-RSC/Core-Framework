public final class GetObjectAt extends Script {

    public GetObjectAt(Extension ex) {
        super(ex);
    }
    
    @Override
    public void init(String params) {
        String[] split = params.split(",");
        System.out.println(getObjectIdFromCoords(
                Integer.parseInt(split[0]),
                Integer.parseInt(split[1])));
    }
}
