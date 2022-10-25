import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MovieAnalyzer {
    public static List<Movie> movies = new ArrayList<Movie>();
    public static class Movie {
        private String Series_Title;
        private int Released_Year;
        private String Certificate;
        private int Runtime;
        private String[] Genre;
        private float IMDB_Rating;
        private String Overview;
        private String Meta_score;
        private String Director;
        private String[] Stars;
        private int Noofvotes;
        private String Gross;
        public Movie(String Series_Title, int Released_Year, String Certificate, int Runtime, String Genre, float IMDB_Rating
                , String Overview, String Meta_score, String Director, String[] Stars, int Noofvotes, String Gross)
        {
            this.Series_Title = Series_Title.replaceAll("\"", "");
            this.Released_Year = Released_Year;
            this.Certificate = Certificate;
            this.Runtime = Runtime;
            this.Genre = Genre.replaceAll("\"", "").replaceAll(" ", "").split(",");
            this.IMDB_Rating = IMDB_Rating;
            this.Overview = Overview;
            this.Meta_score = Meta_score;
            this.Director = Director;
            this.Stars = Stars;
            this.Noofvotes = Noofvotes;
            this.Gross = Gross;
        }
        public int getReleased_Year(){return Released_Year;}
        public String[] getGenre(){return Genre;}
        public int getRuntime(){return Runtime;}
        public String getSeries_Title(){return Series_Title;}
        public String getOverview(){return Overview;}
        public int getOverviewLength(){
            if(Overview.startsWith("\"")){
                return Overview.length() - 2;
            }
            return Overview.length();
        }
        public float getIMDB_Rating(){return IMDB_Rating;}
        public String[] getStars(){return Stars;}
        public String getGross(){return Gross;}
    }

    public static class Star {
        String Name;
        double Rating;
        long Gross;

        public Star(String Name, double Rating){
            this.Name = Name;
            this.Rating = Rating;
        }
        public Star(String Name, long Gross){
            this.Name = Name;
            this.Gross = Gross;
        }
        public String getName(){return Name;}
        public double getRating(){return Rating;}
        public long getGross(){return Gross;}
    }
    public static class GenreCount{
        String Name;
        int count;
        public GenreCount(String Name, int count){
            this.Name = Name;
            this.count = count;
        }
        public String getName(){return Name;}
        public int getCount(){return count;}
    }
    public static class CoStarCount{
        List<String> starsName;
        int count;
        public CoStarCount(List<String> starsName, int count){
            this.starsName = starsName;
            this.count = count;
        }
        public List<String> getStarsName(){return starsName;}
        public String getFirstStar(){return starsName.get(0);}
        public String getSecondStar(){return starsName.get(1);}
        public int getCount(){return count;}
    }
    public MovieAnalyzer(String dataset_path) throws IOException {
        Files.lines(Paths.get(dataset_path))
                .skip(1)
                .map(l -> l.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1))
                .map(a -> new Movie(a[1], Integer.parseInt(a[2]), a[3], Integer.parseInt(a[4].split(" ")[0]), a[5],
                        Float.parseFloat(a[6]), a[7], a[8], a[9], new String[]{a[10], a[11], a[12], a[13]}
                        , Integer.parseInt(a[14]), a[15])).forEach(a ->movies.add(a));
    }
    public Map<Integer, Integer> getMovieCountByYear(){
        Stream<Movie> moviesStream = movies.stream();
        Map<Integer, Long> map = moviesStream.collect(Collectors.groupingBy(Movie::getReleased_Year, Collectors.counting()));
        Set<Integer> set = map.keySet();
        Map<Integer, Integer> ans = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        });
        set.forEach(a -> {
            Long ansOut = map.get(a);
            int anin = ansOut.intValue();
            ans.put(a, anin);
        });
        return ans;
    }
    public Map<String, Integer> getMovieCountByGenre(){
        Stream<Movie> moviesStream = movies.stream();
        Map<String,Integer> MovieGenre = new HashMap<String,Integer>();
        List<GenreCount> genremap = new ArrayList<GenreCount>();
        Map<String,Integer> ans = new LinkedHashMap<String,Integer>();
        moviesStream.forEach(a -> {
            String[] genres = a.getGenre();
            for (String genre : genres) {
                MovieGenre.put(genre, MovieGenre.getOrDefault(genre, 0) + 1);
            }
        });
        Set<String> set = MovieGenre.keySet();
        for(String str : set){
            genremap.add(new GenreCount(str, MovieGenre.get(str)));
        }
        genremap.sort(Comparator.comparing(GenreCount::getCount, Comparator.reverseOrder()).thenComparing(GenreCount::getName));
        for (GenreCount genreCount : genremap) {
            ans.put(genreCount.getName(), genreCount.getCount());
        }
        return ans;
    }
    public String addList(String s1 , String s2){
        if(s1.compareTo(s2) >= 0){
            return s2 + "," + s1;
        }
        return s1 + "," + s2;
    }
    public Map<List<String>, Integer> getCoStarCount(){
        Stream<Movie> moviesStream = movies.stream();
        Map<String, Integer> CoStarmap = new HashMap<String, Integer>();
        List<CoStarCount> res = new ArrayList<CoStarCount>();
        Map<List<String>, Integer> ans = new LinkedHashMap<List<String>, Integer>();
        moviesStream.forEach(a -> {
            String Costar_1 = addList(a.getStars()[0], a.getStars()[1]);
            String Costar_2 = addList(a.getStars()[0], a.getStars()[2]);
            String Costar_3 = addList(a.getStars()[0], a.getStars()[3]);
            String Costar_4 = addList(a.getStars()[1], a.getStars()[2]);
            String Costar_5 = addList(a.getStars()[1], a.getStars()[3]);
            String Costar_6 = addList(a.getStars()[2], a.getStars()[3]);
            CoStarmap.put(Costar_1, CoStarmap.getOrDefault(Costar_1, 0) + 1);
            CoStarmap.put(Costar_2, CoStarmap.getOrDefault(Costar_2, 0) + 1);
            CoStarmap.put(Costar_3, CoStarmap.getOrDefault(Costar_3, 0) + 1);
            CoStarmap.put(Costar_4, CoStarmap.getOrDefault(Costar_4, 0) + 1);
            CoStarmap.put(Costar_5, CoStarmap.getOrDefault(Costar_5, 0) + 1);
            CoStarmap.put(Costar_6, CoStarmap.getOrDefault(Costar_6, 0) + 1);
        });
        Set<String> set = CoStarmap.keySet();
        for(String str : set){
            res.add(new CoStarCount(new ArrayList<>(Arrays.asList(str.split(","))), CoStarmap.get(str)));
        }
        res.sort(Comparator.comparing(CoStarCount::getFirstStar).thenComparing(CoStarCount::getSecondStar));
        for(CoStarCount coStarCount : res){
            ans.put(coStarCount.getStarsName(), coStarCount.getCount());
        }
        return ans;
    }
    public List<String> getTopMovies(int top_k, String by){
        Stream<Movie> moviesStream = movies.stream();
        List<String> ans = new ArrayList<String>();
        if(by.equals("runtime")){
            moviesStream.sorted(Comparator.comparing(Movie::getRuntime, Comparator.reverseOrder()).
                    thenComparing(Movie::getSeries_Title)).limit(top_k).forEach(a -> {
                ans.add(a.getSeries_Title());
            });
        }else{
            moviesStream.sorted(Comparator.comparing(Movie::getOverviewLength, Comparator.reverseOrder()).
                    thenComparing(Movie::getSeries_Title)).limit(top_k).forEach(a -> {ans.add(a.getSeries_Title());});
        }
        return ans;
    }
    public List<String> getTopStars(int top_k, String by){
        Stream<Movie> moviesStream = movies.stream();
        List<String> ans = new ArrayList<String>();
        List<Star> starmap = new ArrayList<Star>();
        if(by.equals("rating")){
            Map<String,List<Float>> starRating =new HashMap<>();
            moviesStream.forEach(a -> {
                if(!starRating.containsKey(a.getStars()[0])) starRating.put(a.getStars()[0], new ArrayList<Float>());
                if(!starRating.containsKey(a.getStars()[1])) starRating.put(a.getStars()[1], new ArrayList<Float>());
                if(!starRating.containsKey(a.getStars()[2])) starRating.put(a.getStars()[2], new ArrayList<Float>());
                if(!starRating.containsKey(a.getStars()[3])) starRating.put(a.getStars()[3], new ArrayList<Float>());
                starRating.get(a.getStars()[0]).add(a.getIMDB_Rating());
                starRating.get(a.getStars()[1]).add(a.getIMDB_Rating());
                starRating.get(a.getStars()[2]).add(a.getIMDB_Rating());
                starRating.get(a.getStars()[3]).add(a.getIMDB_Rating());
            });
            Set<String> set = starRating.keySet();
            for(String str : set){
                List<Float> temp = starRating.get(str);
                double rating = 0.0;
                for(float rat : temp){
                    rating += rat;
                }
                rating = rating / temp.size();
                starmap.add(new Star(str, rating));
            }
            starmap.sort(Comparator.comparing(Star::getRating, Comparator.reverseOrder()).thenComparing(Star::getName));
            for(int i = 0 ; i < top_k ; i++){
                ans.add(starmap.get(i).getName());
            }
        }else{
            Map<String,List<Long>> starRating =new HashMap<>();
            moviesStream.forEach(a -> {
                if(!a.getGross().equals("")){
                    long aGross = Long.parseLong(a.getGross().replaceAll(",","").
                            replaceAll("\"",""));
                    if(!starRating.containsKey(a.getStars()[0])) starRating.put(a.getStars()[0], new ArrayList<>());
                    if(!starRating.containsKey(a.getStars()[1])) starRating.put(a.getStars()[1], new ArrayList<>());
                    if(!starRating.containsKey(a.getStars()[2])) starRating.put(a.getStars()[2], new ArrayList<>());
                    if(!starRating.containsKey(a.getStars()[3])) starRating.put(a.getStars()[3], new ArrayList<>());
                    starRating.get(a.getStars()[0]).add(aGross);
                    starRating.get(a.getStars()[1]).add(aGross);
                    starRating.get(a.getStars()[2]).add(aGross);
                    starRating.get(a.getStars()[3]).add(aGross);
                }
            });
            Set<String> set = starRating.keySet();
            for(String str : set){
                List<Long> temp = starRating.get(str);
                long grossAll = 0;
                for(long gross : temp){
                    grossAll += gross;
                }
                grossAll = grossAll / temp.size();
                starmap.add(new Star(str, grossAll));
            }
            starmap.sort(Comparator.comparing(Star::getGross, Comparator.reverseOrder()).thenComparing(Star::getName));
            for(int i = 0 ; i < top_k ; i++){
                ans.add(starmap.get(i).getName());
            }
        }
        return ans;
    }
    public List<String> searchMovies(String genre, float min_rating, int max_runtime){
        Stream<Movie> moviesStream = movies.stream();
        List<String> ans = new ArrayList<String>();
        moviesStream.filter(a -> {
            String[] genres = a.getGenre();
            for (String s : genres) {
                if (s.equals(genre)) {
                    return true;
                }
            }
            return false;
        }).filter(a -> a.getIMDB_Rating() >= min_rating
        ).filter(a -> a.getRuntime() <= max_runtime).forEach(a -> ans.add(a.getSeries_Title()));
        ans.sort(String::compareTo);
        return ans;
    }
}