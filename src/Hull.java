
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Klasa porównująca po zmiennej: kąt nachylenie 
 * @author Michal
 */
class CompareByAngle implements Comparator<Point2D> 
{
    /**
     * komparator
     * @param o1 obiekt 1
     * @param o2 obiekt 2
     * @return wynik
     */
    @Override
    public int compare(Point2D o1, Point2D o2) 
    {
        return o1.Angle.compareTo(o2.Angle);
    }
}


/**
 * Klasa oslugujaca dane, które są następnie przekazane do
 * rozwiązania właściwym algorytmem
 * @author Michal
 */
public class Hull
{
    /**
     * lista wczytanych punktów w 2D
     */
    private List<Point2D> points2D;  
    
    /**
     * konstruktor inicjalizujacy
     * @param points punkty 2D
     */
    public Hull(List<Point2D> points)
    {
        this.points2D = points;
    }
    
    /**
     * Funkcja obslugujaca rozwiazanie problemu
     * @return pole
     */
    public Double ResolveProblem()
    {
        QuickHullAlgorithm qh;
        IrregularPolygonArea ip;
        
        qh = new QuickHullAlgorithm(points2D);
        qh.Go();
        ip = new IrregularPolygonArea(qh.getResultPoints());

        return ip.Resolve();
    }
}




/**
 * klasa reprezentujaca algorytm QuickHull
 * więcej o nim: https://pl.wikipedia.org/wiki/Quickhull
 * @author Michal
 */
class QuickHullAlgorithm
{
    /**
     * punkty wejsciowe
     */
    private Point2D[] points;
    
    /**
     * punkty wynikowe
     * otoczka ...
     */
    private List<Point2D> resultPoints;
   
    /**
     * konstruktor inicjalizujacy
     * @param points 
     */
    public QuickHullAlgorithm(List<Point2D> points)
    {
        this.points = points.toArray(new Point2D[points.size()]);
        resultPoints = new ArrayList<>();
    }

    /**
     * ustawienie parametrow poczatkowych i odpalenie alg
     */
    public void Go()
    {
        QuickConvexHull();
    }
    
    /**
     * szukanie wypuklych punktow
     */
    private void QuickConvexHull()
    {
        // znalezienie dwoch punktow: prawy dol i lewy gora
	int right, left;
	right = left = 0;
	for ( int i = 1; i < points.length; i++ ) 
        {
	    if ( ( points[right].X > points[i].X ) 
                    || ( ( Math.abs(points[right].X - points[i].X) < 0.001 ) && ( points[right].Y > points[i].Y ) ))
		right = i;
	    if ( ( points[left].X < points[i].X ) 
                    || ( ( Math.abs(points[left].X - points[i].X) < 0.001 ) && ( points[left].Y < points[i].Y ) ))
		left = i;
	}

        if(Main.getTest())
            System.out.println("l: "+left+", r: "+right);

	List<Integer> aLeft1 = new ArrayList<>();
	List<Integer> aLeft2 = new ArrayList<>();

	Float upper;
	for ( int i = 0; i < points.length; i++ ) 
        {
	    if ( (i == left) || (i == right) )
		continue;
	    upper = isOnRight(right,left,i);
	    if ( upper > 0 )
		aLeft1.add(i);
	    else if ( upper < 0 )
		aLeft2.add(i);
	}

        Point2D new1 = new Point2D(points[right].X, points[right].Y, 0f);        
        getResultPoints().add(new1);
	QuickHull(right, left, aLeft1);
        
        Point2D new2 = new Point2D(points[left].X, points[left].Y, 0f);
        getResultPoints().add(new2);
	QuickHull(left, right, aLeft2);
    }
    
    
    /**
     * sprawdzamy czy pkt p jest po prawej od linii a-b
     * @param a pkt
     * @param b pkt
     * @param p pkt
     * @return wynik
     */
    private Float isOnRight(int a, int b, int p)
    {
	return ((points[a].X - points[b].X)
                *(points[p].Y - points[b].Y))
                - ((points[p].X - points[b].X)
                *(points[a].Y - points[b].Y));
    }
    
    /**
     * odleglosc (kwadratowa) pkt p do lini a-b
     * @param a pkt
     * @param b pkt
     * @param p pkt
     * @return wynik
     */
    private Float DistanceFromLineToPoint(int a, int b, int p)
    {
	Float x, y, u;
	u = ((points[p].X - points[a].X)*(points[b].X - points[a].X) + (points[p].Y - points[a].Y)*(points[b].Y - points[a].Y)) 
	    / ((points[b].X - points[a].X)*(points[b].X - points[a].X) + (points[b].Y - points[a].Y)*(points[b].Y - points[a].Y));
	x = points[a].X + u * (points[b].X - points[a].X);
	y = points[a].Y + u * (points[b].Y - points[a].Y);
	return ((x - points[p].X)*(x - points[p].X) + (y - points[p].Y)*(y - points[p].Y));
    }
    
    /**
     * najdalszy pkt 
     * @param a pkt
     * @param b pkt
     * @param al lista pkt
     * @return wynik;
     */
    private int FarthestPoint(int a, int b, List<Integer>al)
    {
	Float maxDistance, distance;
	int maxPoint, point;
	maxDistance = -1.0f;
	maxPoint = -1;
	for ( int i = 0; i < al.size(); i++ ) 
        {
	    point = al.get(i);
	    if ( (point == a) || (point == b) )
		continue;
	    distance = DistanceFromLineToPoint(a, b, point);
	    if ( distance > maxDistance ) 
            {
		maxDistance = distance;
		maxPoint = point;
	    }
	}
	return maxPoint;
    }
    
    /**
     * wlasciwy algorytm znajdowania pkt ...
     * @param a pkt
     * @param b pkt
     * @param al lista pkt
     */
    private void QuickHull(int a, int b, List<Integer> al)
    {
	if(Main.getTest())
            System.out.println("a:"+a+",b:"+b+" size: "+al.size());
	
        if ( al.isEmpty() )
	    return;

	int c, p;

	c = FarthestPoint(a, b, al);

	List<Integer> al1 = new ArrayList<>();
	List<Integer> al2 = new ArrayList<>();

	for ( int i=0; i<al.size(); i++ ) 
        {
	    p = al.get(i);
	    if ( (p == a) || (p == b) )
		continue;
	    if ( isOnRight(a,c,p) > 0 )
		al1.add(p);
	    else if ( isOnRight(c,b,p) > 0 )
		al2.add(p);
	}
        
        
	QuickHull(a, c, al1);
        Point2D new1 = new Point2D(points[c].X, points[c].Y, 0f);
        getResultPoints().add(new1);
	QuickHull(c, b, al2);
    }

    /**
     * @return the resultPoints
     */
    public List<Point2D> getResultPoints()
    {
        return resultPoints;
    }
}


/**
 * Klasa obliczajaca pole nieregularnego poligonu zlozonego z pkt 2D
 * @author Michal
 */
class IrregularPolygonArea
{
    /**
     * tablica punktow
     */
    public Point2D[] points;
    
    /**
     * pole poligonu
     */
    Double AreaOfPolygon;

    /**
     * konsktruktor inicjalizujacy
     * @param points punkty wejsciowe z ktorym bedzie liczone pole
     */
    public IrregularPolygonArea(List<Point2D> points)
    {
        this.points = points.toArray(new Point2D[points.size()]);;
        AreaOfPolygon = 0.0;
    }
    
    /**
     * funkcja oblugujaca rozwiazywanie problemu - pole poligonu
     * @return 
     */
    public double Resolve()
    {
        SortCornersInCounterClockwiseDirection();
        AreaOfPolygon = PolygonArea();
        return AreaOfPolygon;
    }
    
    /**
     * sortowanie podanych punktow względem pkt środkowego
     * godnie z ruchem wskazowek zegara
     */
    private void SortCornersInCounterClockwiseDirection()
    {
        int ilosc = points.length;
        
        double  centerX = 0.0,
                centerY = 0.0;
        for(Point2D i : points)
        {
            centerX+=i.X;
            centerY+=i.Y;
        }
        
        centerX/=(double)ilosc;
        centerY/=(double)ilosc;
        
        List<Point2D> newPoints = new ArrayList<>();
        
        for(Point2D i : points)
        {
            double an = (Math.atan2(i.Y - centerY, i.X - centerX) + 2.0 * Math.PI) % (2.0 * Math.PI);
            newPoints.add(new Point2D(i.X, i.Y, an));
        }
        
        Collections.sort(newPoints, new CompareByAngle());
        points = newPoints.toArray(new Point2D[newPoints.size()]);
    }
    
    /**
     * Obliczanie pola poligonu
     * @return pole poligonu
     */
    private double PolygonArea()
    {
        int ilosc = points.length;
        double area = 0.0;
        
        int j = 0;
        for(int i=0;i<ilosc;++i)
        {
            j = (i + 1) % ilosc;
            area += points[i].X * points[j].Y;
            area -= points[j].X * points[i].Y;
        }
        
        area = Math.abs(area) / 2.0;
        
        return area;
    }
}


/**
 * Klasa punkt
 * zawiera potrzebne informacje do przechowania pkt 2D
 * @author Michal
 */
class Point2D
{
    /**
     * X
     */
    public Float X;
    
    /**
     * Y
     */
    public Float Y;

    /**
     * kąt
     */
    public Double Angle;
    
    /**
     * konstruktor inicjaluzujacy
     * @param X X
     * @param Y Y
     * @param angle kąt
     */
    public Point2D(Float X, Float Y, double angle)
    {
        this.X = X;
        this.Y = Y;
        this.Angle = angle;
    }

    /**
     * konstruktor inicjaluzujacy
     * @param X X
     * @param Y Y
     */
    public Point2D(Float X, Float Y)
    {
        this.X = X;
        this.Y = Y;
    }   
}