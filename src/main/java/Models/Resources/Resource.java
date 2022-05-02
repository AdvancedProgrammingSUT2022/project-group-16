package Models.Resources;

import Models.City.Product;

public abstract class Resource implements Product
{
	protected final ResourceType RESOURCE_TYPE;
	
	protected Resource(ResourceType resource_type)
	{
		RESOURCE_TYPE = resource_type;
	}
	
	public ResourceType getRESOURCE_TYPE()
	{
		return RESOURCE_TYPE;
	}
}
