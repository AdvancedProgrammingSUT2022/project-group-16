package Models.Resources;

public class BonusResource extends Resource
{
	public BonusResource(ResourceType resourceType)
	{
		super(resourceType);
	}

	public BonusResource clone()
	{
		return new BonusResource(this.RESOURCE_TYPE);
	}
}
