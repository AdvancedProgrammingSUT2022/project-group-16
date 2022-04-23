import Controllers.GameController;
import Views.registerAndLoginView;

public class Main
{
	public static void main(String[] args)
	{
//		registerAndLoginView.run();
		GameController gameController = GameController.getInstance();
		gameController.getMapString();
	}
}
