package Models.Resources;

public class StrategicResource extends Resource
{
	public StrategicResource(ResourceType resourceType)
	{
		super(resourceType);
	}

	public StrategicResource clone()
	{
		return new StrategicResource(this.RESOURCE_TYPE);
	}
}
