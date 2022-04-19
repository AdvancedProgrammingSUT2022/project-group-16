package Models.Resources;

public abstract class Resource
{
	protected final ResourceType RESOURCE_TYPE;
	
	protected Resource(ResourceType resource_type)
	{
		RESOURCE_TYPE = resource_type;
	}
}
