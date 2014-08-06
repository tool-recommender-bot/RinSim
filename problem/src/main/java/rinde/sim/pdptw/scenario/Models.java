package rinde.sim.pdptw.scenario;

import javax.annotation.Nullable;
import javax.measure.Measure;
import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;

import rinde.sim.core.graph.Point;
import rinde.sim.core.model.Model;
import rinde.sim.core.model.pdp.DefaultPDPModel;
import rinde.sim.core.model.pdp.PDPModel;
import rinde.sim.core.model.pdp.TimeWindowPolicy;
import rinde.sim.core.model.road.PlaneRoadModel;
import rinde.sim.core.model.road.RoadModel;
import rinde.sim.pdptw.common.PDPRoadModel;

import com.google.common.base.Objects;
import com.google.common.base.Supplier;

/**
 * Utility class for creating {@link Supplier}s that create {@link Model}s which
 * can be used in a {@link ScenarioGenerator}.
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 */
public final class Models {

  private Models() {}

  /**
   * Creates a new supplier for creating a {@link PlaneRoadModel} wrapped in a
   * {@link PDPRoadModel}. All constructor parameters except
   * <code>maxSpeed</code> and <code>allowDiversion</code> are supplied by the
   * {@link ScenarioGenerator}. The bounds of the plane, the speed unit and the
   * distance unit are supplied by the {@link ScenarioGenerator}.
   * @param maxSpeed The maximum speed to use. See the {@link PlaneRoadModel}
   *          constructor for more information. The unit of the speed is defined
   *          by the {@link ScenarioGenerator} instance.
   * @param allowDiversion Whether to allow diversion. See the
   *          {@link PDPRoadModel} constructor for more information.
   * @return A supplier which can be used in a {@link ScenarioGenerator}. See
   *         {@link ScenarioGenerator.Builder#addModel(ModelSupplierScenGen)}.
   */
  public static ModelSupplierScenGen<RoadModel> roadModel(double maxSpeed,
      boolean allowDiversion) {
    return new DefaultRoadModelSupplier(maxSpeed, allowDiversion);
  }

  /**
   * Creates a new supplier for creating a {@link PDPModel}.
   * @param twp The {@link TimeWindowPolicy} to use.
   * @return A supplier for {@link PDPModel}.
   */
  public static Supplier<PDPModel> pdpModel(TimeWindowPolicy twp) {
    return new PDPModelSupplier(twp);
  }

  /**
   * Wraps the specified supplier in a {@link ModelSupplierScenGen}.
   * @param sup The supplier.
   * @param <T> The model type.
   * @return The wrapped supplier.
   */
  public static <T extends Model<?>> ModelSupplierScenGen<T> adapt(
      Supplier<T> sup) {
    return new Adapter<T>(sup);
  }

  /**
   * A supplier which can be used to construct a {@link Model} based on a
   * {@link ScenarioGenerator}.
   * @param <T> The type of model the supplier returns.
   * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
   */
  public interface ModelSupplierScenGen<T extends Model<?>> {
    /**
     * @param sg The {@link ScenarioGenerator} that can be used in the supplier.
     * @return An instance of <code>T</code>. This <b>must</b> be a newly
     *         constructed instance.
     */
    Supplier<T> get(ScenarioGenerator sg);
  }

  private static class Adapter<T extends Model<?>> implements
      ModelSupplierScenGen<T> {
    private final Supplier<T> supplier;

    Adapter(Supplier<T> sup) {
      supplier = sup;
    }

    @Override
    public Supplier<T> get(ScenarioGenerator sg) {
      return supplier;
    }
  }

  private static class PDPModelSupplier implements Supplier<PDPModel> {
    private final TimeWindowPolicy timeWindowPolicy;

    PDPModelSupplier(TimeWindowPolicy twp) {
      timeWindowPolicy = twp;
    }

    @Override
    public PDPModel get() {
      return new DefaultPDPModel(timeWindowPolicy);
    }

    @Override
    public String toString() {
      return Objects.toStringHelper(this)
          .add("timeWindowPolicy", timeWindowPolicy)
          .toString();
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(timeWindowPolicy);
    }

    @Override
    public boolean equals(@Nullable Object other) {
      if (this == other) {
        return true;
      }
      if (null == other || getClass() != other.getClass()) {
        return false;
      }
      final PDPModelSupplier o = (PDPModelSupplier) other;
      return Objects.equal(o.timeWindowPolicy, timeWindowPolicy);
    }
  }

  private static class DefaultRoadModelSupplier implements
      ModelSupplierScenGen<RoadModel> {
    final double speed;
    final boolean diversion;

    DefaultRoadModelSupplier(double maxSpeed, boolean allowDiversion) {
      speed = maxSpeed;
      diversion = allowDiversion;
    }

    @Override
    public Supplier<RoadModel> get(final ScenarioGenerator sg) {
      return new RoadModelSup(speed, diversion, sg);
    }
  }

  private static class RoadModelSup implements Supplier<RoadModel> {
    private final Point min;
    private final Point max;
    private final Unit<Length> distanceUnit;
    private final Measure<Double, Velocity> speedMeasure;
    private final boolean allowDiversion;

    RoadModelSup(double speed, boolean diversion, ScenarioGenerator sg) {
      min = sg.getMin();
      max = sg.getMax();
      distanceUnit = sg.getDistanceUnit();
      speedMeasure = Measure.valueOf(speed, sg.getSpeedUnit());
      allowDiversion = diversion;
    }

    @Override
    public RoadModel get() {
      return new PDPRoadModel(
          new PlaneRoadModel(
              min,
              max,
              distanceUnit,
              speedMeasure),
          allowDiversion);
    }

    @Override
    public String toString() {
      return Objects.toStringHelper(this)
          .add("min", min)
          .add("max", max)
          .add("distanceUnit", distanceUnit)
          .add("speedMeasure", speedMeasure)
          .add("allowDiversion", allowDiversion)
          .toString();
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(min, max, distanceUnit, speedMeasure,
          allowDiversion);
    }

    @Override
    public boolean equals(@Nullable Object other) {
      if (this == other) {
        return true;
      }
      if (null == other) {
        return false;
      }
      if (getClass() != other.getClass()) {
        return false;
      }
      final RoadModelSup o = (RoadModelSup) other;
      return Objects.equal(o.min, min)
          && Objects.equal(o.max, max)
          && Objects.equal(o.distanceUnit, distanceUnit)
          && Objects.equal(o.speedMeasure, speedMeasure)
          && Objects.equal(o.allowDiversion, allowDiversion);
    }
  }
}
