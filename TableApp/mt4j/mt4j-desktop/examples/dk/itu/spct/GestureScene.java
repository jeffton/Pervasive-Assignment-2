package dk.itu.spct;

import java.util.ArrayList;
import java.util.List;

import org.mt4j.AbstractMTApplication;
import org.mt4j.MTApplication;

import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeEvent;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.Direction;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.UnistrokeGesture;
import org.mt4j.input.inputProcessors.globalProcessors.RawFingerProcessor;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import dk.itu.spct.SceneUtilities.TapAction;

public class GestureScene extends AbstractScene {
	private AbstractMTApplication _application;
	private ArrayList<Vector3D> points;
	private RawFingerProcessor ra;
	private MTTextArea _textArea;
	private final AbstractScene _absScene;
	GestureScene myself;
	MTPolygon visualizer;

	public GestureScene(AbstractMTApplication mtapp, String name, AbstractScene absScene) {
		super(mtapp, name);
		points = new ArrayList<Vector3D>();

		_application = mtapp;
		myself = this;
		_absScene = absScene;
		this.setClearColor(new MTColor(66, 66, 66));
		MTRectangle rect = new MTRectangle(mtapp, 0, 0, 100, 100);
		rect.unregisterAllInputProcessors();
		rect.removeAllGestureEventListeners();

		visualizer = new MTPolygon(mtapp, new Vertex[] { new Vertex(0, 0),
				new Vertex(1, 1), new Vertex(2, 2) });
		visualizer.setPickable(false);
		visualizer.setDepthBufferDisabled(false);
		addControls();
		getPoints();
	}

	/**
	 * Add buttons to the scene
	 */
	public void addControls() {
		int right = _application.getWidth();
		int bottom = _application.getHeight();

		SceneUtilities.addButton(this, "images/save.png",
				new Vector3D(964, 100), new TapAction() {
					@Override
					public void onTap() {
						myself.unregisterGlobalInputProcessor(ra);
						System.out.println(points.size());

						// Deletes that last point as it is at the same
						// point as this button
						points.remove(points.size() - 1);
						getCanvas().removeChild(visualizer);
						
						UnistrokeProcessor up = new UnistrokeProcessor(
								_application);
						up.getUnistrokeUtils()
								.getRecognizer()
								.addTemplate(UnistrokeGesture.CUSTOMGESTURE,
										points, Direction.CLOCKWISE);
						getCanvas().registerInputProcessor(up);
						_absScene.getCanvas().registerInputProcessor(up);

					}
				});

		SceneUtilities.addButton(this, "images/clear.png", new Vector3D(964,
				170), new TapAction() {
			@Override
			public void onTap() {
				points.clear();
				getCanvas().removeChild(visualizer);
				System.out.println("Gesture have been deleted");
			}
		});

		SceneUtilities.addButton(this, "images/testgesture.png", new Vector3D(
				964, 240), new TapAction() {
			@Override
			public void onTap() {
				testStroke();
			}
		});
		SceneUtilities.addButton(this, "images/backButton.png", new Vector3D(
				60, bottom - 35), new TapAction() {
			@Override
			public void onTap() {
				_application.popScene();
			}
		});
		addTextArea();

	}

	public void testStroke() {

		getCanvas().addGestureListener(UnistrokeProcessor.class,
				new IGestureEventListener() {
					public boolean processGestureEvent(MTGestureEvent ge) {
						UnistrokeEvent ue = (UnistrokeEvent) ge;
						switch (ue.getId()) {
						case UnistrokeEvent.GESTURE_STARTED:
							getCanvas().addChild(ue.getVisualization());
							break;
						case UnistrokeEvent.GESTURE_UPDATED:
							break;
						case UnistrokeEvent.GESTURE_ENDED:
							UnistrokeGesture g = ue.getGesture();
							if(g.equals(UnistrokeGesture.CUSTOMGESTURE)){
								showText("Gesture recongnized");
							}else{
								showText("Gesture NOT recongnized");
							}
							System.out.println("Recognized gesture: " + g);
							break;
						default:
							break;
						}
						return false;
					}
				});
	}

	/**
	 * Starts registers all movements on the screen.
	 * @return
	 */
	public List<Vector3D> getPoints() {
		ra = new RawFingerProcessor();

		final ArrayList<Vertex> vs = new ArrayList<Vertex>();
		System.out.println("Starter her");
		ra.addProcessorListener(new IMTInputEventListener() {

			@Override
			public boolean processInputEvent(MTInputEvent inEvt) {
				Vertex[] v;
				MTPolygon pol;
				final AbstractCursorInputEvt posEvt = (AbstractCursorInputEvt) inEvt;
				points.add(posEvt.getPosition());
				vs.add(new Vertex(posEvt.getPosition()));
				v = new Vertex[vs.size()];
				pol = new MTPolygon(_application, vs.toArray(v));
				
				if (v != null)
					visualizer.setVertices(v);
				visualizer.setNoFill(true);
				visualizer.setStrokeWeight(5);
				visualizer.setStrokeColor(new MTColor(255, 255, 0, 192));
				getCanvas().addChild(visualizer);
				return false;
			}
		});
		this.registerGlobalInputProcessor(ra);

		return null;
	}

	  private void addTextArea() {
		    IFont textFont = FontManager.getInstance().createFont(_application,
		        "arial.ttf", 30, new MTColor(255, 255, 255));
		    _textArea = new MTTextArea(_application, textFont);
		    _textArea.setNoStroke(true);
		    _textArea.setNoFill(true);
		    _textArea.unregisterAllInputProcessors();
		    _textArea.removeAllGestureEventListeners();
		    getCanvas().addChild(_textArea);
		  }

	  private void showText(final String text) {
		    _application.invokeLater(new Runnable() {
		      @Override
		      public void run() {
		        _textArea.setText(text);
		        _textArea.setPositionGlobal(new Vector3D(_application.getWidth() / 2,
		            _application.getHeight() / 3));
		      }
		    });
		  }
}
