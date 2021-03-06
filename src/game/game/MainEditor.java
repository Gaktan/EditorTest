package game.game;

import game.panels.Frame;

import java.awt.Canvas;

public class MainEditor {

	static {
		// System.loadLibrary("openGL32");
	}

	public static void main(String... args) {
		int width = 800;
		int height = 600;
		boolean fullScreen = false;
		boolean ignoreNext = false;

		for (int i = 0; i < args.length; i++) {
			if (ignoreNext) {
				ignoreNext = false;
				continue;
			}

			String arg = args[i].trim().toLowerCase();

			if (arg.equals("-skipmenu")) {
				GameEditor.SKIP_MENU = true;
				System.out.println("Skipping menu");
			}
			else if (arg.equals("-width")) {
				if (i > args.length - 1) {
					System.err.println("Error with parameter -width. Usage : -width value.");
					return;
				}

				String s_width = args[i + 1];

				try {
					width = Integer.parseInt(s_width);
					ignoreNext = true;
				} catch (NumberFormatException e) {
					System.err.println("Error with parameter -width. Usage : -width value. Value must be an integer");
					return;
				}
			}
			else if (arg.equals("-height")) {
				if (i > args.length - 1) {
					System.err.println("Error with parameter -height. Usage : -height value.");
					return;
				}

				String s_height = args[i + 1];

				try {
					height = Integer.parseInt(s_height);
					ignoreNext = true;
				} catch (NumberFormatException e) {
					System.err.println("Error with parameter -height. Usage : -height value. Value must be an integer");
					return;
				}
			}
			else if (arg.equals("-fullscreen")) {
				System.out.println("Fullscreen mode");
				fullScreen = true;
			}
			else if (arg.equals("-help")) {
				StringBuilder sb = new StringBuilder();
				sb.append("Available commands:").append("\n");
				sb.append("-help").append("\n\t").append("displays this message").append("\n");
				sb.append("-fullscreen").append("\n\t").append("sets the game in fulscreen").append("\n");
				sb.append("-width x").append("\n\t").append("sets the window width (where x is an integer)")
						.append("\n");
				sb.append("-height y").append("\n\t").append("sets the window height (where y is an integer)")
						.append("\n");
				sb.append("-skipmenu").append("\n\t").append("Skips the main menu and starts the game right away")
						.append("\n");
				sb.append("-debug").append("\n\t").append("Displays debug messages").append("\n");

				System.out.println(sb.toString());
			}
			else if (arg.equals("-debug")) {
				GameEditor.DEBUG = true;
			}
			else {
				System.err.println("Unknown parameter " + arg + ".");
			}
		}

		Frame f = new Frame(width, height);
		Canvas parent = f.openGLCanvas;

		new GameEditor(width, height, fullScreen, parent);
	}
}
