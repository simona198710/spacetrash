package com.svamp.spacetrash;

/**
 * Created by Simon on 2016-08-09.
 */
/*
 * Solar.java
 *
 * Created: 10-Nov-2009
 *
 * Author: Hj. Malthaner
 * Email:  h_malthaner@users.sourceforge.net
 *
 * See license.txt for license details
 */


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Solar systems will be represented by tree structures of
 * solar objects. Class was derived from older code of mine
 * and didn't get cleaned up very much ...
 *
 * @author Hj. Malthaner
 */
public class Solar
{

    /**
     * Types of a body in stellar systems
     */
    public enum BodyType
    {
        SUN,
        PLANET,
        STATION,
        SPACEPORT, // Hajo: a bit of a hack here, spaceports are like stations
        // but on the surface of planets.
    };

    /**
     * Types of planets
     */
    public enum PlanetType
    {
        BARE_ROCK, // Rocky, (almost) no atmosphere
        ICE,       // Ice body
        ATM_ROCK,  // Rocky with thin atmosphere
        CLOUD,     // Venus type
        EARTH,     // Earth type
        SMALL_GAS, // Small gas giant
        BIG_GAS,   // Big gas giant
        RINGS,     // Planet with rings
        GRAPHITE_ROCK,  // High carbon rocky planet
        STATION_1, // Type 1 space station
        SPACEPORT, // Space port
    };

    /**
     * Types of suns
     */
    public enum SunType
    {
        S_YELLOW,
        S_ORANGE,
        S_WHITE_DWARF,
        S_RED_GIANT,
        S_BLUE_GIANT,
        S_NEUTRON,
        S_BLACK_HOLE,
        S_BROWN_DWARF,
    };

    public final static String planetDescription[] =
            {
                    "Type:<font color=white> Bare rock body</font>",
                    "Type:<font color=white> Ice body</font>",
                    "Type:<font color=white> Rock body with thin atmosphere</font>",
                    "Type:<font color=white> Rock body with a dense atmosphere</font>",
                    "Type:<font color=white> Earthlike planet</font>",
                    "Type:<font color=white> Small gas giant</font>",
                    "Type:<font color=white> Big gas giant</font>",
                    "Type:<font color=white> Big planet with rings</font>",
                    "Type:<font color=white> Carbon rich planet</font>",
                    "Type:<font color=white> Space station</font>",
                    "Type:<font color=white> Space port</font>",
            };

    public final static String sunDescription[] =
            {
                    "A medium size yellow star",
                    "A small orange star",
                    "A white dwarf star",
                    "A red giant star",
                    "A giant blue star",
                    "A neutron star",
                    "A stellar black hole",
                    "A brown dwarf",
            };


    // Tables of star and spaceport data

    private static final double sunSurfaceTemperature [] =
            {
                    6500,    // Our sun
                    5500,    // Orange sun
                    100000,  // White dwarf ??
                    5000,    // Red giant
                    12000,   // Blue giant
                    1000000, // Neutron star
                    0,       // Black hole
                    1200,    // Brown dwarf
            };

    /**
     * Sun densities. Only data for our sun is real.
     * Given in g/cm³
     */
    private static final double sunDensity [] =
            {
                    1.408,     // Our sun
                    1.6,       // Orange star - unkown, placeholder
                    1000000.0, // White dwarf
                    0.003,     // Red giant
                    0.1,       // Blue giant
                    11E5,      // Neutron star
                    11E14,     // Black hole - unknown, placeholder
                    5.0,       // brown dwarf
            };

    /**
     * Planet densities.
     * Given in g/cm³
     */
    private static final double planetDensity [] =
            {
                    5.427,  // Merkur
                    1.750,  // Pluto (ice body)
                    3.933,  // Mars
                    5.243,  // Venus
                    5.515,  // Earth
                    1.638,  // Neptun
                    1.326,  // Jupiter
                    0.687,  // Saturn
                    4.000,  // Carbon rock
                    3.000,  // Space station
            };


    private short[][] planetPos = new short[1024][1024];
    //private byte[][] planetIDs = new byte[1024][1024];
    //private byte[][] moonIDtoPlanetID = new byte[1024][1024];
    //private byte[][][] moonIDs = new byte[1024][1024][128];
    private String[][] bodyMergedData = new String[1024][1024];
    public int regionX;
    public int regionY;

    /**
     * Calculate sun radius from sun type
     */
    private double calcSunRadius(SunType type)
    {
        // Hajo: Safe default
        final double mod = 0.5 - rng.nextDouble();
        final double Ro = 1.3914e6 / 2.0;

        double rad;

        switch (type) {
            case S_YELLOW:
                rad = Ro * (1+mod);
                break;
            case S_ORANGE:
                rad = Ro * (1+mod) / 2;
                break;
            case S_BLUE_GIANT:
                rad = Ro * (1+mod) * 5;
                break;
            case S_RED_GIANT:
                rad = Ro * (1+mod) * 15;
                break;
            case S_WHITE_DWARF:
                rad = 1390600 / (190+(0.5*mod)*110);
                break;
            case S_NEUTRON:
                rad = 11000.0 + 1000.0 * mod;
                break;
            case S_BLACK_HOLE:
                rad = 40.0 + 40.0 * mod;
                break;
            case S_BROWN_DWARF:
                rad = 120000.0 + 120000.0 * mod;
                break;
            default:
                rad = 1390600.0;
        }

        return rad;
    }


    private static double calcVolume(double radius)
    {
        return (4.0 * Math.PI * radius * radius * radius)/3.0;
    }


    private static double calcPlanetMass(PlanetType ptype, double radius)
    {
        // Hajo: Volume in km³, radius is in km.
        final double v = calcVolume(radius);

        // 1km = 100.000 cm = 1*10^6 cm

        // 1 ton = 1000kg = 1.000.000g

        // 1m³ = 1.000.000cm

        // x [g/cm³] = x [t/m³]

        // 1km³ = 1.000.000.000m³


        double mass = 1.0E9 * v * planetDensity[ptype.ordinal()];

        return mass;
    }

    private static double calcSunMass(SunType stype, double radius)
    {
        // Hajo: Volume in km³, radius is in km.
        final double v = calcVolume(radius);
        final double m = v * sunDensity[stype.ordinal()];
        final double mass = 1.0E9 * m;

        return mass;
    }

    /**
     * System location in the galaxy (if part of a galaxy)
     */
    //public final int[][] loca;
    public final SystemLocation loca;

    // Hajo: double linked tree structure.
    private Solar parent;
    public ArrayList<Solar> children;

    /**
     * Body type
     */
    public BodyType btype;

    /**
     * If btype == P_PLANET or btype == P_STATION then PType will tell
     * the type of the spaceport
     */
    public PlanetType ptype;

    /**
     * If btype == P_SUN then SType will tell the type of the sun
     */
    public SunType stype;

    /**
     * The rng to create this system. All bodies share the same rng.
     */
    Random rng;

    /**
     * Radius of this stellar body in km.
     */
    public int radius;

    /**
     * Mass of this stellar body
     */
    public double mass;

    /**
     * Orbital height in km - do not change after creation of a system.
     */
    public double orbit;

    /**
     * Rotation period (day-length) in seconds.
     */
    public double rotationPeriod;

    /**
     * Angle on orbit. 0 means 3 o' Clock.
     */
    public double orbitAngle;

    /**
     * Relative position towards parent body.
     */
    public Vec3 pos;

    /**
     * Earth Equiv. Temp. - a rough guess of temperature,
     * based on earth type planet characteristics.
     */
    public int eet;

    /**
     * Body name
     */
    public String name;

    /**
     * If this is a dual star system, the base name is the name without alpha/beta
     */
    public String baseName;

    /**
     * Society information for populated space bodies.
     */
    public Society society;


    /**
     * Biospehere (lifeform) information
     */
    //public final Biosphere biosphere;

    /**
     * A seed value that can later be used to create random but
     * deterministic values for this body. This seed cannot be used to
     * recreate the body data itself!
     */
    public long seed;

    /**
     * Determine a type for the sun of this system
     * May only be called once!
     * @author Hj. Malthaner
     */
    private SunType randomSunType()
    {
        int [] weights =
                {
                        16, // Yellow sun
                        28, // Orange star
                        12, // White dwarf frequency ??
                        8, // Red giant
                        4, // Blue giant
                        2, // Neutron star
                        1, // Black hole
                        8, // Brown dwarf
                };

        final int index = Utils.oneOfWeightedList(rng, weights);
        return SunType.values()[index];
    }

    final void calcPType()
    {
        ptype = PlanetType.BARE_ROCK; // default
        double temperatureFactor = 1.0;

        if (radius < 2500) {

            if (eet > 200) {
                if(rng.nextDouble() > 0.05)
                {
                    ptype = PlanetType.BARE_ROCK;
                    temperatureFactor = 0.9;
                }
                else
                {
                    ptype = PlanetType.GRAPHITE_ROCK;
                    temperatureFactor = 1.2;
                }
            } else {
                ptype = PlanetType.ICE;
                temperatureFactor = 0.8;
            }
        } else if (radius < 3600) {
            if (eet > 190) {
                if(rng.nextDouble() > 0.05)
                {
                    ptype = PlanetType.ATM_ROCK;
                }
                else
                {
                    ptype = PlanetType.GRAPHITE_ROCK;
                    temperatureFactor = 1.2;
                }
            } else {
                ptype = PlanetType.ICE;
            }
        }
        else if (radius < 12000)
        {
            if(rng.nextDouble() < 0.03)
            {
                ptype = PlanetType.GRAPHITE_ROCK;
                temperatureFactor = 1.2;
            }
            else if (eet > 275 && eet < 355)
            {
                if (rng.nextDouble() > 0.75) {
                    ptype = PlanetType.CLOUD;
                    temperatureFactor = 1.1;
                } else {
                    ptype = PlanetType.EARTH;
                }
            } else if (eet > 150 && eet < 800) {
                ptype = PlanetType.CLOUD;
                temperatureFactor = 1.2;
            } else if (eet <= 150) {
                ptype = PlanetType.ICE;
            } else {
                ptype = PlanetType.BARE_ROCK;
            }

        }
        else if (radius < 30000)
        {
            ptype = PlanetType.SMALL_GAS;
            temperatureFactor = 1.15;

            // we need a few exceptions for superheavy rock bodies
            if(rng.nextDouble() > 0.87)
            {
                if(eet > 150 && eet < 800)
                {
                    ptype = PlanetType.CLOUD;
                    temperatureFactor = 1.2;
                }
                else if(eet > 100)
                {
                    if(rng.nextDouble() < 0.03)
                    {
                        ptype = PlanetType.GRAPHITE_ROCK;
                        temperatureFactor = 1.2;
                    }
                    else
                    {
                        ptype = PlanetType.ATM_ROCK;
                        temperatureFactor = 1.0;
                    }
                } else if(eet > 1200) {
                    ptype = PlanetType.BARE_ROCK;
                    temperatureFactor = 0.9;
                } else {
                    ptype = PlanetType.ICE;
                    temperatureFactor = 0.8;
                }
            }
        }
        else if (radius < 65000)
        {
            ptype = PlanetType.RINGS;
            temperatureFactor = 1.20;

            // we need a few exceptions for superheavy rock bodies
            if(rng.nextDouble() > 0.88)
            {
                if(eet > 150 && eet < 800)
                {
                    ptype = PlanetType.CLOUD;
                    temperatureFactor = 1.2;
                }
                else if(eet > 100)
                {
                    if(rng.nextDouble() < 0.03)
                    {
                        ptype = PlanetType.GRAPHITE_ROCK;
                        temperatureFactor = 1.2;
                    }
                    else
                    {
                        ptype = PlanetType.ATM_ROCK;
                        temperatureFactor = 1.0;
                    }
                }
                else if(eet > 1200)
                {
                    ptype = PlanetType.BARE_ROCK;
                    temperatureFactor = 0.9;
                }
                else
                {
                    ptype = PlanetType.ICE;
                    temperatureFactor = 0.8;
                }
            }

        }
        else
        {
            ptype = PlanetType.BIG_GAS;
            temperatureFactor = 1.50;
        }

        // Hajo: Apply temperature modifier
        eet *= temperatureFactor;

        // check for space station
        if (btype == BodyType.STATION)
        {
            ptype = PlanetType.STATION_1;
        }
    }

    private static double normverteilung(double x, double u, double s)
    {
        final double e = Math.exp(-((x - u) * (x - u)) / (2.0 * s * s));
        final double d = Math.sqrt(2.0 * Math.PI * s * s);

        return e / d;
    }

    /**
     * Eart Equiv. Temp.
     *
     * Berechnet Temperatur der Erde in Kelvin, waere sie an Stelle dieses Planeten
     * Das ist eine grobe Schaetzung.
     */
    static double calcEET(SunType stype, double sun_rad, double sun_dist)
    {
        // return Math.atan(sun_rad/sun_dist)*62000.0;

        double area = sun_rad * sun_rad;
        double d2 = sun_dist*sun_dist;

        double t = sunSurfaceTemperature[stype.ordinal()];

        // Hajo: incoming "heat"
        // double t_in  = (t * area / d2) * 800000;
        double t_in  = (t * area / d2) * 300000000;

        // Hajo: assuming that warmer planets lose energy by radiation
        // and that total temperature is proportional to sqrt(t)
        //
        // Also added an absolute offset, due to "inner warmth" of the body.
        // return Math.sqrt(t_in) + 25;
        return Math.pow(t_in, 0.333333) + 25;
    }

    /**
     * Build a new spaceport/body for a stellar system
     *
     * @param minOrbit minimum orbital radius for this body
     * @param size    amount of random spreading in orbit radius
     * @param number  this is the number'th child of parent, counting from 0
     * @paran planets     total number of children of parent
     *
     * @author Hj. Malthaner
     */
    public Solar(Solar p, double minOrbit, int size, int number, int anz, BodyType aBtype)
    {
        //this.biosphere = new Biosphere();
        this.loca = p.loca;

        children = new ArrayList();
        pos = new Vec3();
        parent = p;
        rng = parent.rng;

        btype = aBtype;

        if (btype == BodyType.STATION)
        {
            // Hajo: will be named in populateAux
            name = "unnamed";
            ptype = PlanetType.STATION_1;
        }
        else
        {
            final String base;

            if(parent.name.endsWith("Alpha"))
            {
                base = parent.name.substring(0, parent.name.length() - 6);
            }
            else
            {
                base = parent.name;
            }

            name = base + " " + (number + 1);
        }

        // System.err.println("Body " + (number+1) + " of " + anz + ", minOrbit=" + minOrbit);

        final double baseSize = normverteilung(number, anz / 1.8, 1.5) * size;
        radius = 493 + (int)(rng.nextDouble() * baseSize);


        //orbit = minOrbit * 1.25 + rng.nextDouble() * Math.min((long)minOrbit * 2, 1000000000L);
        orbit = minOrbit * 1.25 + rng.nextDouble() * Math.min((long)minOrbit * 2, 10000000L);


        // Calc orbit position
        //double u = rng.nextDouble() * 360.0 * Math.PI / 180.0;
        double u = rng.nextInt() * 360.0 * Math.PI / 180.0;
        //double u = (rng.nextInt() - 32768) * 360.0 * Math.PI / 180.0;
        orbitAngle = u;

        pos.x = Math.cos(u) * orbit;
        pos.y = Math.sin(u) * orbit;
        pos.z = 0;

        Solar the_sun = this;
        Vec3 zv = new Vec3();

        while(the_sun.parent != null)
        {
            zv.x += the_sun.pos.x;
            zv.y += the_sun.pos.y;
            zv.z += the_sun.pos.z;

            // printf("%f %f\n", zv_x, zv_y);

            the_sun = the_sun.parent;
        }

        double sun_dist = Math.sqrt(zv.length2());

        if(the_sun.children.size() > 0 &&
                the_sun.children.get(0).btype == BodyType.SUN)
        {
            // Hajo: dual star, add some radiation
            eet = (int)calcEET(the_sun.stype, the_sun.radius*1.8, sun_dist);
        }
        else
        {
            eet = (int)calcEET(the_sun.stype, the_sun.radius, sun_dist);
        }


        calcPType();


        Random rngFix = new Random(seed);
        //System.out.println("Still not hanged");
        rotationPeriod = (rngFix.nextGaussian() * 25) + 25; //hänger sig här?
        //System.out.println("Still not hanged");

        if(rotationPeriod < 4)
        {
            rotationPeriod = rng.nextDouble() * 24 * 365;
        }

        // System.err.println("rotaper=" + rotationPeriod);

        // Hajo: norm period to seconds
        rotationPeriod *= 60.0 * 60.0;

        mass = calcPlanetMass(ptype, radius);
        // System.err.println("Radius=" + radius + "km Mass=" + mass + "t");

        // Moons?
        if (btype != BodyType.STATION &&
                btype != BodyType.SPACEPORT &&
                orbit > 15000 &&
                radius > 2000)
        {
            if (rng.nextDouble() > 0.5)
            {
                int count = Math.min(5, (int)(rng.nextDouble() * radius / 2000.0));
                double minRad = radius * 2;

                for (int i = 0; i < count; i++)
                {
                    Solar planet = new Solar(this, minRad, radius / 2, i, count, BodyType.PLANET);
                    minRad = planet.orbit;
                    children.add(planet);
                }
            }

            final double chance = Society.calcProbability(loca);
            if(rng.nextDouble()*100.0 < (42.0*chance))
            {
                //Solar planet = new Solar(this, radius, radius / 4, 0, 1, BodyType.STATION);
                //planet.radius /= 4;
                //children.add(planet);
            }
        }

        seed = rng.nextLong();
    }

    public Solar getParent()
    {
        return parent;
    }

    public Solar root()
    {
        Solar up = getParent();
        Solar root = this;

        while(up != null)
        {
            root = up;
            up = up.getParent();
        }

        return root;
    }


    /**
     * Build a new stellar system from the seed
     *
     * @author Hj. Malthaner
     */
    public Solar(SystemLocation loca, boolean create_children, int requestedRegionX, int requestedRegionY)
    {
        //this.biosphere = new Biosphere();
        this.loca = loca;

        this.regionX = requestedRegionX;
        this.regionY = requestedRegionY;

        pos = new Vec3();
        children = new ArrayList();
        parent = null;

        rng = RandomHelper.createRNG();

        rng.setSeed(loca.systemSeed);

        // Type of the sun
        stype = randomSunType();
        btype = BodyType.SUN;
        ptype = PlanetType.BARE_ROCK;

        //name = NameGenerator.generateStarName(rng);
        name = "test";
        baseName = name;
        radius = (int)calcSunRadius(stype);
        mass = calcSunMass(stype, radius);

        // System.err.println("Sun type: " + stype.name());
        // System.err.println("Sun radius: " + radius);

        if(create_children)
        {

            // Hajo: do a roll to see if this will become a multi star system
            int suns = (int)(rng.nextDouble()*2);
            suns = createMultipleStars(suns, seed);

            int planets = 1 + (int)(rng.nextDouble() * 12);

            // Hajo: after-nova suns have usually lost some planets
            if(stype == SunType.S_BROWN_DWARF)
            {
                planets /= 5;
            }
            else if(stype == SunType.S_NEUTRON)
            {
                planets /= 6;
            }
            else if(stype == SunType.S_BLACK_HOLE)
            {
                planets /= 8;
            }
            else if(stype == SunType.S_BLUE_GIANT)
            {
                planets += (int)(rng.nextDouble() * 6);
                planets += (int)(rng.nextDouble() * 4);
            }

            double minRad = 36 * radius * (1+suns);

            if(stype == SunType.S_WHITE_DWARF)
            {
                // Hajo: white dwarfs are very small, radius-wise,
                // but they are very heavy, so the inner planets should
                // have bigger radii
                minRad *= 20;
            }
            else if(stype == SunType.S_NEUTRON)
            {
                minRad *= 1000;
            }
            else if(stype == SunType.S_BLACK_HOLE)
            {
                minRad *= 600000;
            }
            else if(stype == SunType.S_BLUE_GIANT && stype == SunType.S_RED_GIANT)
            {
                minRad *= 0.5;
            }

            //System.out.println("Creating planets");
            createPlanetsWithRegion(planets, minRad);
            //createPlanets(5, minRad);
        }

        seed = rng.nextLong();
    }

    public Solar(SystemLocation loca, boolean create_children)
    {
        //this.biosphere = new Biosphere();
        this.loca = loca;

        pos = new Vec3();
        children = new ArrayList();
        parent = null;

        rng = RandomHelper.createRNG();

        rng.setSeed(loca.systemSeed);

        // Type of the sun
        stype = randomSunType();
        btype = BodyType.SUN;
        ptype = PlanetType.BARE_ROCK;

        //name = NameGenerator.generateStarName(rng);
        name = "test";
        baseName = name;
        radius = (int)calcSunRadius(stype);
        mass = calcSunMass(stype, radius);

        // System.err.println("Sun type: " + stype.name());
        // System.err.println("Sun radius: " + radius);

        if(create_children)
        {

            // Hajo: do a roll to see if this will become a multi star system
            int suns = (int)(rng.nextDouble()*2);
            suns = createMultipleStars(suns, seed);

            int planets = 1 + (int)(rng.nextDouble() * 12);

            // Hajo: after-nova suns have usually lost some planets
            if(stype == SunType.S_BROWN_DWARF)
            {
                planets /= 5;
            }
            else if(stype == SunType.S_NEUTRON)
            {
                planets /= 6;
            }
            else if(stype == SunType.S_BLACK_HOLE)
            {
                planets /= 8;
            }
            else if(stype == SunType.S_BLUE_GIANT)
            {
                planets += (int)(rng.nextDouble() * 6);
                planets += (int)(rng.nextDouble() * 4);
            }

            double minRad = 36 * radius * (1+suns);

            if(stype == SunType.S_WHITE_DWARF)
            {
                // Hajo: white dwarfs are very small, radius-wise,
                // but they are very heavy, so the inner planets should
                // have bigger radii
                minRad *= 20;
            }
            else if(stype == SunType.S_NEUTRON)
            {
                minRad *= 1000;
            }
            else if(stype == SunType.S_BLACK_HOLE)
            {
                minRad *= 600000;
            }
            else if(stype == SunType.S_BLUE_GIANT && stype == SunType.S_RED_GIANT)
            {
                minRad *= 0.5;
            }

            //System.out.println("Creating planets");
            createPlanets(planets, minRad);
            //createPlanets(5, minRad);
        }

        seed = rng.nextLong();
    }


    private int createMultipleStars(int suns, long seed)
    {
        for (int i = 0; i < suns; i++) {
            // Find a matching type for the first sun

            SunType [] options;

            switch(stype) {
                case S_BLUE_GIANT:
                    options = new SunType [] { };
                    break;
                case S_ORANGE:
                    options = new SunType [] {
                            SunType.S_ORANGE, SunType.S_WHITE_DWARF, SunType.S_BROWN_DWARF
                    };
                    break;
                case S_RED_GIANT:
                    options = new SunType []
                            {
                                    SunType.S_YELLOW, SunType.S_ORANGE, SunType.S_WHITE_DWARF, SunType.S_BROWN_DWARF, SunType.S_NEUTRON
                            };
                    break;
                case S_WHITE_DWARF:
                    options = new SunType [] {
                            SunType.S_WHITE_DWARF, SunType.S_NEUTRON
                    };
                    break;
                case S_YELLOW:
                    options = new SunType [] {
                            SunType.S_YELLOW, SunType.S_ORANGE, SunType.S_WHITE_DWARF, SunType.S_BROWN_DWARF
                    };
                    break;
                case S_NEUTRON:
                case S_BLACK_HOLE:
                case S_BROWN_DWARF:
                    options = new SunType [] { };
                    break;
                default:
                    options = new SunType [0];
            }

            if(options.length == 0)
            {
                suns = 0;
            }
            else
            {
                SunType sunType = options[(int)(rng.nextDouble() * options.length)];
                double sunRad = calcSunRadius(sunType);

                // Adjust radius so that the primary star is bigger
                double factor = radius / sunRad;
                factor = Math.min(0.95, factor * 0.8);
                sunRad *= factor;

                // Hajo: preserve seed for later use
                long oldSeed = loca.systemSeed;
                loca.systemSeed += 1234567890;
                Solar sunBeta = new Solar(loca, false);
                loca.systemSeed = oldSeed;

                sunBeta.stype = sunType;
                sunBeta.radius = (int)sunRad;
                sunBeta.orbit = (sunRad + radius) * 3;
                sunBeta.orbitAngle = 5;
                sunBeta.mass = calcSunMass(sunType, sunBeta.radius);

                sunBeta.pos.x = Math.cos(sunBeta.orbitAngle) * sunBeta.orbit;
                sunBeta.pos.y = Math.sin(sunBeta.orbitAngle) * sunBeta.orbit;
                sunBeta.pos.z = 0;

                children.add(sunBeta);

                baseName = name;
                sunBeta.name = name + " Beta";
                name = name + " Alpha";
            }
        }

        return suns;
    }

    private void createPlanets(int planets, double minOrbit)
    {
        int counter = 1;
        for (int i = 0; i < planets && minOrbit > 0; i++) {
            //System.out.println("Creating planet");
            Solar planet = new Solar(this, minOrbit, 380000, i, planets, BodyType.PLANET);
            minOrbit = planet.orbit;

            //System.out.println((long) planet.orbit);
            //int newPlanetPosX = (int)(long) planet.orbit / 10000000;
            //int newPlanetPosY = (int)(long) planet.orbitAngle / 10000000;

            int centerX = 2048;
            int centerY = 2048;
            double xoff = planet.pos.x;
            double yoff = planet.pos.y;
            double scale = 0.05;


            final int w = 2 + (int) (planet.radius * 1);
            long x = centerX + (long) (xoff * scale);
            long y = centerY + (long) (yoff * scale);
            //long x = centerX - w/2 + (long)(xoff * scale);
            //int y = centerY - w/2 + (int)(yoff * scale);

            long newPlanetPosX = x;
            long newPlanetPosY = y;
            if (planet.orbitAngle < 0){
                //newPlanetPosX = -newPlanetPosX;
            }
            newPlanetPosX = (newPlanetPosX / 1000000) + centerX;

            //System.out.println("Rotation Period: " + (planet.orbitAngle));
            System.out.println("X: " + (newPlanetPosX));
            System.out.println("Y: " + (newPlanetPosY));

            /*
            for(Solar moon : planet.children){
                scale = 0.25;
                double moonxoff = moon.pos.x + planet.pos.x;
                double moonyoff = moon.pos.y + planet.pos.y;

                long moonx = centerX + (long) (moonxoff * scale);
                long moony = centerY + (long) (moonyoff * scale);

                long newMoonPosX = moonx;
                long newMoonPosY = moony;
                if (moon.orbitAngle < 0){
                    //newMoonPosX = -x;
                }
                System.out.println("Moon X: " + ((newMoonPosX / 1000000) + centerX));
                System.out.println("Moon Y: " + (newMoonPosY));
            }
            */

            //System.out.println("");
            //planetPos[(int)newPlanetPosX][(int)newPlanetPosY] = 1;
            //planetIDs[(int)newPlanetPosX][(int)newPlanetPosY] = (byte)counter;

            children.add(planet);
            counter = counter +1;
        }
    }

    private void createPlanetsWithRegion(int planets, double minOrbit)
    {
        int counter = 1;
        for (int i = 0; i < planets && minOrbit > 0; i++) {
            //System.out.println("Creating planet");
            Solar planet = new Solar(this, minOrbit, 380000, i, planets, BodyType.PLANET);
            minOrbit = planet.orbit;

            //System.out.println((long) planet.orbit);
            //int newPlanetPosX = (int)(long) planet.orbit / 10000000;
            //int newPlanetPosY = (int)(long) planet.orbitAngle / 10000000;

            int mapsize = 32768;
            byte numRegions = 32;
            long centerX = mapsize /2;
            long centerY = mapsize /2;
            double xoff = planet.pos.x;
            double yoff = planet.pos.y;
            double scale = 15;


            //final int w = 2 + (int) (planet.radius * 1);
            long x = centerX + (long) (xoff * scale);
            long y = centerY + (long) (yoff * scale);
            //long x = centerX - w/2 + (long)(xoff * scale);
            //int y = centerY - w/2 + (int)(yoff * scale);

            long newPlanetPosX = x;
            long newPlanetPosY = y;
            if (planet.orbitAngle < 0){
                //newPlanetPosX = -newPlanetPosX;
            }
            newPlanetPosX = (newPlanetPosX / 1000000) + centerX;

            //System.out.println("Rotation Period: " + (planet.orbitAngle));
            System.out.println();
            System.out.println();
            System.out.println("Planet " + planet.name + " Seed: " + planet.seed + " ID: " + (counter -1));
            System.out.println("At real pos X: " + ((int)newPlanetPosX) + " = " + (((int)newPlanetPosX) - ((mapsize / numRegions) * (regionX))));
            System.out.println("At real pos Y: " + ((int)newPlanetPosY) + " = " + (((int)newPlanetPosY) - ((mapsize / numRegions) * (regionY))));
            System.out.println();

            int moonCounter = 1;
            for(Solar moon : planet.children) {
                //scale = 0.25;
                long moonxoff = (long) moon.pos.x + (long) planet.pos.x;
                double moonyoff = moon.pos.y + planet.pos.y;

                long moonx = centerX + (long) (moonxoff * scale);
                double moony = centerY + (moonyoff * scale);

                long newMoonPosX = moonx;
                long newMoonPosY = (long) moony;
                if (moon.orbitAngle < 0) {
                    //newMoonPosX = -x;
                }

                newMoonPosX = (newMoonPosX / 1000000) + centerX;
                System.out.println("Moon " + moon.name + " Seed: " + moon.seed + " ID: " + (moonCounter - 1));
                System.out.println("At real pos X: " + ((long) newMoonPosX) + " = " + (((long) newMoonPosX) - ((mapsize / numRegions) * (regionX))));
                System.out.println("At real pos Y: " + ((long) newMoonPosY) + " = " + (((long) newMoonPosY) - ((mapsize / numRegions) * (regionY))));
                System.out.println();

                if ((int) newMoonPosX < (mapsize / numRegions) * (regionX + 1) && (int) newMoonPosX >= (mapsize / numRegions) * (regionX)) {
                    if ((int) newMoonPosY < (mapsize / numRegions) * (regionY + 1) && (int) newMoonPosY >= (mapsize / numRegions) * (regionY)) {
                        System.out.println("Moon " + moon.name + " Seed: " + moon.seed + " ID: " + (moonCounter - 1));
                        System.out.println("At real pos X: " + ((int) newMoonPosX) + " = " + (((int) newMoonPosX) - ((mapsize / numRegions) * (regionX))));
                        System.out.println("At real pos Y: " + ((int) newMoonPosY) + " = " + (((int) newMoonPosY) - ((mapsize / numRegions) * (regionY))));
                        System.out.println();
                        planetPos[((int) newMoonPosX) - ((mapsize / numRegions) * (regionX))][((int) newMoonPosY) - ((mapsize / numRegions) * (regionY))] = 2;
                        //moonIDtoPlanetID[((int) newMoonPosX) - ((mapsize / numRegions) * (regionX))][((int) newMoonPosY) - ((mapsize / numRegions) * (regionY))] = (byte)counter;
                        //moonIDs[((int) newMoonPosX) - ((mapsize / numRegions) * (regionX))][((int) newMoonPosY) - ((mapsize / numRegions) * (regionY))][counter] = (byte)moonCounter;
                        bodyMergedData[((int)newMoonPosX) - ((mapsize / numRegions) * (regionX))][((int)newMoonPosY) - ((mapsize / numRegions) * (regionY))] = (counter)+":"+(moonCounter);

                    }
                }
                moonCounter = moonCounter +1;
            }


            //System.out.println("");

            children.add(planet);

            if ((int)newPlanetPosX < (mapsize / numRegions) * (regionX +1) && (int)newPlanetPosX >= (mapsize / numRegions) * (regionX)){
                if ((int)newPlanetPosY < (mapsize / numRegions) * (regionY +1) && (int)newPlanetPosY >= (mapsize / numRegions) * (regionY)){
                    System.out.println("Planet " + planet.name + " Seed: " + planet.seed + " ID: " + (counter -1));
                    System.out.println("At real pos X: " + ((int)newPlanetPosX) + " = " + (((int)newPlanetPosX) - ((mapsize / numRegions) * (regionX))));
                    System.out.println("At real pos Y: " + ((int)newPlanetPosY) + " = " + (((int)newPlanetPosY) - ((mapsize / numRegions) * (regionY))));
                    System.out.println();
                    planetPos[((int)newPlanetPosX) - ((mapsize / numRegions) * (regionX))][((int)newPlanetPosY) - ((mapsize / numRegions) * (regionY))] = 1;
                    bodyMergedData[((int)newPlanetPosX) - ((mapsize / numRegions) * (regionX))][((int)newPlanetPosY) - ((mapsize / numRegions) * (regionY))] = (counter)+":"+(0);

                }
                //System.out.println("Value X betwen: " + (1024 * (regionX)) + " and " + (1024 * (regionX +1)));
                //System.out.println("Value Y betwen: " + (1024 * (regionY)) + " and " + (1024 * (regionY +1)));
            }

            counter = counter +1;
        }
    }

    public double calcSurfaceGravity()
    {
        // g = H*M/R²
        final double h = 6.674E-11;
        final double m = mass * 1000; // kg
        final double r = radius * 1000; // m
        final double gravity =  h * m / (r * r);

        return gravity;
    }

    public void listSettlements(List<Solar> list)
    {
        for(Solar body : children)
        {
            if(body.btype == BodyType.STATION || body.btype == BodyType.SPACEPORT)
            {
                list.add(body);
            }

            body.listSettlements(list);
        }
    }

    public Vec3 getAbsolutePosition()
    {
        Vec3 absPos = new Vec3(pos);
        Solar p = getParent();

        while(p != null)
        {
            absPos.add(p.pos);
            p = p.getParent();
        }

        return absPos;
    }


    /**
     * Searches a body withing range around pos
     *
     * @author Hj. Malthaner
     */
    public Solar findInRange(Vec3 point_pos, double maxRange)
    {
        double range = 10;
        Solar result;

        do {
            result = findInRangeAux(point_pos, range);
            range *= 10;
        } while(result == null && range < maxRange);

        return result;
    }

    private Solar findInRangeAux(Vec3 point_pos, double range)
    {
        Vec3 v = new Vec3(pos);

        v.sub(point_pos);

        Solar result = null;

        double dist = Math.sqrt(v.length2());
        if(dist < range) {
            // System.err.println("Found " + name + " in range " + dist);
            range = dist;
            result = this;
        }

        Vec3 npos = new Vec3(point_pos);

        npos.sub(pos);

        for(int i=0; i < children.size(); i++) {
            Solar test = children.get(i).findInRangeAux(npos, range);

            if (test != null) {
                result = test;
            }
        }
        return result;
    }

    public short[][] returnPlanetPos(){
        return planetPos;
    }
    public String[][] returnMergedInfo(){
        return bodyMergedData;
    }
    /*
    public byte[][] returnPlanetIDs(){
        return planetIDs;
    }
    public byte[][][] returnMoonIDs(){
        return moonIDs;
    }
    public byte[][] returnMoonIDtoPlanetID(){
        return moonIDtoPlanetID;
    }
    */
}

