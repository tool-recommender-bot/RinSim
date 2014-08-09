package com.github.rinde.rinsim.pdptw.scenario;

import static com.github.rinde.rinsim.util.StochasticSuppliers.constant;
import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.nCopies;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import com.github.rinde.rinsim.core.pdptw.VehicleDTO;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.scenario.AddVehicleEvent;
import com.github.rinde.rinsim.util.StochasticSupplier;
import com.github.rinde.rinsim.util.TimeWindow;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * Utility class for creating {@link VehicleGenerator}s.
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 */
public final class Vehicles {
  static final int DEFAULT_NUM_OF_VEHICLES = 10;
  static final StochasticSupplier<Integer> DEFAULT_NUMBER_OF_VEHICLES = constant(DEFAULT_NUM_OF_VEHICLES);
  static final StochasticSupplier<Double> DEFAULT_SPEED = constant(50d);
  static final StochasticSupplier<Integer> DEFAULT_CAPACITY = constant(1);
  static final StochasticSupplier<Long> DEFAULT_TIME = constant(-1L);

  private Vehicles() {}

  /**
   * Creates a {@link VehicleGenerator} that creates a single vehicle as
   * specified by the {@link VehicleDTO}.
   * @param dto The vehicle specification.
   * @return The newly created generator.
   */
  public static VehicleGenerator single(VehicleDTO dto) {
    return homogenous(dto, 1);
  }

  /**
   * Creates a {@link VehicleGenerator} that creates identical (homogenous)
   * vehicles as specified by the {@link VehicleDTO}.
   * @param dto The vehicle specification.
   * @param numberOfVehicles The number of vehicles to generate.
   * @return The newly created generator.
   */
  public static VehicleGenerator homogenous(VehicleDTO dto, int numberOfVehicles) {
    return new HomogenousVehicleGenerator(numberOfVehicles, dto);
  }

  /**
   * @return A newly constructed {@link Builder} for constructing
   *         {@link VehicleGenerator}s.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Generator of {@link AddVehicleEvent}s.
   * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
   */
  public interface VehicleGenerator {
    /**
     * Should generate a list of {@link AddVehicleEvent}. Each event indicates
     * the addition of a vehicle.
     * @param seed The random seed to use for generating the vehicles.
     * @param center The center of the environment.
     * @param scenarioLength The length of the scenario for which the vehicles
     *          are generated.
     * @return A list of events.
     */
    ImmutableList<AddVehicleEvent> generate(long seed, Point center,
        long scenarioLength);
  }

  /**
   * A builder for constructing {@link VehicleGenerator}s.
   * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
   */
  public static class Builder {
    StochasticSupplier<Integer> numberOfVehicles;
    Optional<StochasticSupplier<Point>> startPositionsSupplier;
    StochasticSupplier<Double> speedsSupplier;
    StochasticSupplier<Integer> capacitiesSupplier;
    Optional<StochasticSupplier<TimeWindow>> timeWindowsSupplier;
    StochasticSupplier<Long> creationTimesSupplier;

    Builder() {
      numberOfVehicles = DEFAULT_NUMBER_OF_VEHICLES;
      startPositionsSupplier = Optional.absent();
      speedsSupplier = DEFAULT_SPEED;
      capacitiesSupplier = DEFAULT_CAPACITY;
      timeWindowsSupplier = Optional.absent();
      creationTimesSupplier = DEFAULT_TIME;
    }

    /**
     * Sets the number of vehicles that are to be created by the generator.
     * Default value: 10. All values returned by the {@link StochasticSupplier} must be
     * greater than <code>0</code>.
     * @param num The supplier to draw numbers from.
     * @return This, as per the builder pattern.
     */
    public Builder numberOfVehicles(StochasticSupplier<Integer> num) {
      numberOfVehicles = num;
      return this;
    }

    /**
     * Sets the start positions of the vehicles that are generated by the
     * generator. By default the start positions of the vehicles are set to the
     * center of the area.
     * @param pos The supplier to draw positions from.
     * @return This, as per the builder pattern.
     */
    public Builder startPositions(StochasticSupplier<Point> pos) {
      startPositionsSupplier = Optional.of(pos);
      return this;
    }

    /**
     * Sets the start positions of all generated vehicles to the center of the
     * area. This is the default behavior.
     * @return This, as per the builder pattern.
     */
    public Builder centeredStartPositions() {
      startPositionsSupplier = Optional.absent();
      return this;
    }

    /**
     * Sets the availability {@link TimeWindow}s of the vehicles that are
     * generated by the generator. By default the time windows of the vehicles
     * are set equal to the time window of the scenario.
     * @param tw The supplier to draw time windows from.
     * @return This, as per the builder pattern.
     */
    public Builder timeWindows(StochasticSupplier<TimeWindow> tw) {
      timeWindowsSupplier = Optional.of(tw);
      return this;
    }

    /**
     * Sets the availability {@link TimeWindow}s of the vehicles that are
     * generated by the generator to be equal to that of the scenario.
     * @return This, as per the builder pattern.
     */
    public Builder timeWindowsAsScenario() {
      timeWindowsSupplier = Optional.absent();
      return this;
    }

    /**
     * Sets the speeds of the vehicles that are generated by the generator.
     * Default value: 50 (using the speed unit of the scenario).
     * @param sp The supplier from which to draw speed values.
     * @return This, as per the builder pattern.
     */
    public Builder speeds(StochasticSupplier<Double> sp) {
      speedsSupplier = sp;
      return this;
    }

    /**
     * Sets the capacities of the vehicles that are generated by the generator.
     * Default value: 1.
     * @param cap The supplier from which to draw capacity values.
     * @return This, as per the builder pattern.
     */
    public Builder capacities(StochasticSupplier<Integer> cap) {
      capacitiesSupplier = cap;
      return this;
    }

    /**
     * Sets the creation times of the vehicles that are generated by the
     * generator. Default value: -1, this means that the vehicles exist at the
     * beginning of the scenario.
     * @param times The supplier from which to draw times.
     * @return This, as per the builder pattern.
     */
    public Builder creationTimes(StochasticSupplier<Long> times) {
      creationTimesSupplier = times;
      return this;
    }

    /**
     * @return A newly constructed {@link VehicleGenerator} as specified by this
     *         builder.
     */
    public VehicleGenerator build() {
      return new DefaultVehicleGenerator(this);
    }
  }

  private static class DefaultVehicleGenerator implements VehicleGenerator {
    private final StochasticSupplier<Integer> numberOfVehicles;
    private final Optional<StochasticSupplier<Point>> startPositionGenerator;
    private final StochasticSupplier<Double> speedGenerator;
    private final StochasticSupplier<Integer> capacityGenerator;
    private final Optional<StochasticSupplier<TimeWindow>> timeWindowGenerator;
    private final StochasticSupplier<Long> creationTimeGenerator;
    private final RandomGenerator rng;

    DefaultVehicleGenerator(Builder b) {
      numberOfVehicles = b.numberOfVehicles;
      startPositionGenerator = b.startPositionsSupplier;
      speedGenerator = b.speedsSupplier;
      capacityGenerator = b.capacitiesSupplier;
      timeWindowGenerator = b.timeWindowsSupplier;
      creationTimeGenerator = b.creationTimesSupplier;
      rng = new MersenneTwister();
    }

    @Override
    public ImmutableList<AddVehicleEvent> generate(long seed, Point center,
        long scenarioLength) {
      rng.setSeed(seed);

      final ImmutableList.Builder<AddVehicleEvent> builder = ImmutableList
          .builder();
      final int num = numberOfVehicles.get(rng.nextLong());
      checkArgument(num > 0,
          "The numberOfVehicles supplier must generate values > 0, found %s.",
          num);
      for (int i = 0; i < num; i++) {
        final Point pos = startPositionGenerator.isPresent()
            ? startPositionGenerator.get().get(rng.nextLong())
            : center;
        final double speed = speedGenerator.get(rng.nextLong());
        checkArgument(
            speed > 0d,
            "The speeds supplier must generate values > 0.0, found %s.",
            speed);
        final int capacity = capacityGenerator.get(rng.nextLong());
        checkArgument(
            capacity >= 0,
            "The capacities supplier must generate non-negative values, found %s.",
            capacity);
        final TimeWindow tw = timeWindowGenerator.isPresent()
            ? timeWindowGenerator.get().get(rng.nextLong())
            : new TimeWindow(0L, scenarioLength);
        final long time = creationTimeGenerator.get(rng.nextLong());
        checkArgument(
            time < scenarioLength,
            "The creationTimes supplier must generate values smaller than the scenarioLength (%s), found %s.",
            scenarioLength, time);
        final VehicleDTO dto = VehicleDTO.builder()
            .startPosition(pos)
            .speed(speed)
            .capacity(capacity)
            .availabilityTimeWindow(tw)
            .build();
        builder.add(new AddVehicleEvent(time, dto));
      }
      return builder.build();
    }
  }

  private static class HomogenousVehicleGenerator implements VehicleGenerator {
    private final VehicleDTO vehicleDto;
    private final int n;
    private final RandomGenerator rng;

    HomogenousVehicleGenerator(int numberOfVehicles, VehicleDTO dto) {
      vehicleDto = dto;
      n = numberOfVehicles;
      rng = new MersenneTwister();
    }

    @Override
    public ImmutableList<AddVehicleEvent> generate(long seed, Point center,
        long scenarioLength) {
      rng.setSeed(seed);
      return ImmutableList
          .copyOf(nCopies(n, new AddVehicleEvent(-1, vehicleDto)));
    }
  }
}
