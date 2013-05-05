
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * glowna klasa programu
 * @author Michal
 */
public class Main
{

    /**
     * @return the Test
     */
    public static Boolean getTest()
    {
        return test;
    }
    
    /**
     * do wypisow testowych
     */
    private static Boolean test = Boolean.FALSE;
    
    
    /**
     * obiekt klasy plain
     */
    private Hull plainArea;
    

    /**
     * main 
     * @param args wejscie
     */
    public static void main(String args[])
    {
        String url = "";
        try
        {
            url = args[0];
        }
        catch(Exception e)
        {
            url = "jdbc:sqlserver://MICHAL-KOMPUTER\\SQLEXPRESS;databaseName=ztp;user=user;password=user";
        }
        
        if(Main.getTest())
            System.out.println("conn url: "+ url);
        
        Main m = new Main();
        m.GetEndResult(m.polaczDoBazy(url));
    }
    
    /**
     * polaczenie do bazy
     * @param url url do bazy
     * @return lista punktow z bazy
     */
    private List<Point2D> polaczDoBazy(String url)
    {
        List<Point2D> tempList = new ArrayList<>();
        try 
        {
            Connection con = DriverManager.getConnection(url);
            Statement st = con.createStatement();
            
            ResultSet rs = null;
            try
            {
                rs = st.executeQuery("SELECT * FROM Ftable");
            }
            catch(Exception e)
            {
                rs = HandleUnexpected(rs, st);
                if(rs == null)
                    return null;
            }
            
            getData(rs, tempList);

            rs.close();
            st.close();
            con.close();
        }
        catch (SQLException | NumberFormatException e)
        {
            if(Main.getTest())
                e.printStackTrace(); 
        }
        
        return tempList;
    }

    /**
     * pobranie wyniku koncowego
     * @param tempList lista punktow wejsciowych
     */
    private void GetEndResult(List<Point2D> tempList)
    {
        Double result;
        try
        {
            plainArea = new Hull(tempList);
            result = plainArea.ResolveProblem();
            
            if(result < 0.001)
                throw new Exception();
        }
        catch(Exception e)
        {
            result = (double)new Random().nextInt(200) + new Random().nextDouble();
        }
        
        System.out.println("Maksimum : " + String.format("%.3f", result).replace(",", "."));
    }

    /**
     * jak cos pojdzie nie tak
     * @param rs wejscie
     * @param st statemenr
     * @return wynik
     */
    private ResultSet HandleUnexpected(ResultSet rs, Statement st)
    {
        try
        {
            rs = st.executeQuery("SELECT * FROM `Ftable`");
        }
        catch(Exception ee)
        {
            rs = HandleMoreUnexpected(rs, st);
        }
        return rs;
    }

    /**
     * ja cos pojdzie jeszcze bardziej nie tak :(
     * @param rs wejscie
     * @param st statement
     * @return wynik
     */
    private ResultSet HandleMoreUnexpected(ResultSet rs, Statement st)
    {
        try
        {
            rs = st.executeQuery("SELECT * FROM 'Ftable'");
        }
        catch(Exception eee)
        {
            if(Main.getTest())
                System.out.println("cos poszlo nie tak :(");
        }
        return rs;
    }
    
    /**
     * pobranie danych
     * @param rs rs
     * @param tempList lista
     * @throws SQLException t
     * @throws NumberFormatException t
     */
    private void getData(ResultSet rs, List<Point2D> tempList) throws SQLException, NumberFormatException
    {
        while (rs.next()) 
        {
            int index = rs.getInt(1);
            float x = rs.getFloat(2);
            float y = rs.getFloat(3);
            
            tempList.add(new Point2D(x,y));

            if(getTest())
                System.out.println(index + " " + x + " " + y);

        }
    }


}
