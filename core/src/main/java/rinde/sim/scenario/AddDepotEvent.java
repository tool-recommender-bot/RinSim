/**
 * 
 */
package rinde.sim.scenario;

import rinde.sim.core.graph.Point;
import rinde.sim.core.model.pdp.PDPScenarioEvent;

/**
 * Event indicating that a depot can be created.
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 */
public class AddDepotEvent extends TimedEvent {

  /**
   * The position where the depot is to be added.
   */
  public final Point position;

  /**
   * Create a new instance.
   * @param t The time at which the event is to be dispatched.
   * @param pPosition {@link #position}
   */
  public AddDepotEvent(long t, Point pPosition) {
    super(PDPScenarioEvent.ADD_DEPOT, t);
    position = pPosition;
  }

}