package Models.Resources;

public class LuxuryResource extends Resource {
    public LuxuryResource(ResourceType resourceType) {
        super(resourceType);
    }

    public LuxuryResource clone() {
        return new LuxuryResource(this.RESOURCE_TYPE);
    }
}
