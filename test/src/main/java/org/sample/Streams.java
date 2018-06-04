package org.sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.*;
import org.openjdk.jmh.annotations.*;

@State(Scope.Benchmark)
public class Streams {
  private double[] values = new double[2000000];
  private double[] numbers = new double[1000];
  private Person[] people = Person.generatePeople(10000);

  {
    for (int i = 0; i < numbers.length; i++) {
      numbers[i] = i;
    }
  }

  @Benchmark
  public double mapReduce() {
    return Arrays.stream(values)
      .map(x -> x + 1)
      .map(x -> x * 2)
      .map(x -> x + 5)
      .reduce(0, Double::sum);
  }

  public static double computeStandardDeviation(double[] numbers) {
    double total = Arrays.stream(numbers).sum();
    double mean = total / numbers.length;
    double stdev = Arrays.stream(numbers)
      .map(x -> x - mean)
      .map(d -> d * d).sum();
    return stdev;
  }

  @Benchmark
  public double standardDeviation() {
    return computeStandardDeviation(numbers);
  }

  @Benchmark
  public double parMapReduce() {
    return Arrays.stream(values).parallel()
      .map(x -> x + 1)
      .map(x -> x * 2)
      .map(x -> x + 5)
      .reduce(0, Double::sum);
  }

  @Benchmark
  public double shortHairYoungsterHeight() {
    double averageAge = Arrays.stream(people)
      .filter(p -> p.getHair() == Hairstyle.SHORT)
      .mapToInt(p -> p.getAge())
      .average().getAsDouble();
    return Arrays.stream(people)
      .filter(p -> p.getHair() == Hairstyle.SHORT)
      .filter(p -> p.getAge() < averageAge)
      .mapToInt(Person::getHeight)
      .average()
      .orElse(0.0);
  }

  @Benchmark
  public double volleyballStars() {
    return Arrays.stream(people)
      .map(p -> new Person(Hairstyle.LONG, p.getAge() + 1, p.getHeight()))
      .filter(p -> p.getHeight() > 198)
      .filter(p -> p.getAge() >= 18 && p.getAge() <= 21)
      .mapToInt(p -> p.getHeight())
      .average().getAsDouble();
  }

  @Benchmark
  public int scrabble() {
    return JavaScrabble.run();
  }
}


enum Hairstyle {
  LONG,
  SHORT
}


class Person {
  static final double LONG_RATIO = 0.4;
  static final int MAX_AGE = 100;
  static final int MAX_HEIGHT = 200;

  private Hairstyle hair;
  private int age;
  private int height;

  public Person(Hairstyle hair, int age, int height) {
    this.hair = hair;
    this.age = age;
    this.height = height;
  }

  public Hairstyle getHair() {
    return hair;
  }

  public int getAge() {
    return age;
  }

  public int getHeight() {
    return height;
  }

  public static Person[] generatePeople(int total) {
    Random random = new Random(10);
    Person[] people = new Person[total];
    for (int i = 0; i < total; i++) {
      people[i] = new Person(
        random.nextDouble() < LONG_RATIO ? Hairstyle.LONG : Hairstyle.SHORT,
        (int)(random.nextDouble() * MAX_AGE),
        (int)(random.nextDouble() * MAX_HEIGHT));
    }
    return people;
  }
}
