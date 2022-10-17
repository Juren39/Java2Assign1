import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MovieAnalyzer {
    Stream<Movie> movies;
    public static class Movie {
        private String Series_Title;
        private int Released_Year;
        private String Certificate;
        private int Runtime;
        private String[] Genre;
        private float IMDB_Rating;
        private String Overview;
        private int Meta_score;
        private String Director;
        private String[] Stars;
        private int Noofvotes;
        private long Gross;
        public Movie(String Series_Title, int Released_Year, String Certificate, int Runtime, String Genre, float IMDB_Rating
        , String Overview, int Meta_score, String Director, String[] Stars, int Noofvotes, long Gross)
        {
            this.Series_Title = Series_Title;
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

//        Series_Title - Name of the movie
//        Released_Year - Year at which that movie released
//        Certificate - Certificate earned by that movie
//        Runtime - Total runtime of the movie
//        Genre - Genre of the movie
//        IMDB_Rating - Rating of the movie at IMDB site
//        Overview - mini story/ summary
//        Meta_score - Score earned by the movie
//        Director - Name of the Director
//        Star1,Star2,Star3,Star4 - Name of the Stars
//        Noofvotes - Total number of votes
//        Gross - Money earned by that movie
        public int getReleased_Year(){return Released_Year;}
        public String[] getGenre(){return Genre;}
        public int getRuntime(){return Runtime;}
        public String getSeries_Title(){return Series_Title;}
        public String getOverview(){return Overview;}
        public float getIMDB_Rating(){return IMDB_Rating;}
        public String[] getStars(){return Stars;}
        public long getGross(){return Gross;}
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
        public int getCount(){return count;}
    }
    public MovieAnalyzer(String dataset_path) throws IOException {
        movies = Files.lines(Paths.get(dataset_path))
                .skip(1)
                .map(l -> l.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1))
                .map(a -> new Movie(a[1], Integer.parseInt(a[2]), a[3], Integer.parseInt(a[4].split(" ")[0]), a[5],
                        Integer.parseInt(a[6]), a[7], Integer.parseInt(a[8]), a[9], new String[]{a[10], a[11], a[12], a[13]}
                        , Integer.parseInt(a[14]), Long.parseLong(a[15].replaceAll(",","").
                        replaceAll("\"",""))));
    }
    public Map<Integer, Integer> getMovieCountByYear(){
        Map<Integer, Long> map = movies.sorted(Comparator.comparing(Movie::getReleased_Year)).collect(Collectors.
                groupingBy(Movie::getReleased_Year, Collectors.counting()));
        Set<Integer> set = map.keySet();
        Map<Integer, Integer> ans = new HashMap<Integer, Integer>();
        set.forEach(a -> {
            Long ansOut = map.get(a);
            int anin = ansOut.intValue();
            ans.put(a, anin);
        });
        return ans;
    }
    public Map<String, Integer> getMovieCountByGenre(){
        Map<String,Integer> MovieGenre = new HashMap<String,Integer>();
        List<GenreCount> genremap = new ArrayList<GenreCount>();
        Map<String,Integer> ans = new HashMap<String,Integer>();
        movies.forEach(a -> {
            String[] genres = a.getGenre();
            for (String genre : genres) {
                MovieGenre.put(genre, MovieGenre.getOrDefault(genre, 0) + 1);
            }
        });
//        Stream<Map.Entry<String, Integer>> stream = MovieGenre.entrySet().stream();
//        return (Map<String, Integer>) stream.sorted(new Comparator<Map.Entry<String, Integer>>() {
//            @Override
//            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
//                int countO1 = o1.getValue();
//                int countO2 = o2.getValue();
//                if(countO1 == countO2) return o1.getKey().compareTo(o2.getKey());
//                else return countO2 - countO1;
//            }
//        });
        Set<String> set = MovieGenre.keySet();
        for(String str : set){
            genremap.add(new GenreCount(str, MovieGenre.get(str)));
        }
        genremap.sort((g1, g2) -> {
            if(g1.getCount() == g2.getCount()) return g1.getName().compareTo(g2.getName());
            return g2.getCount() - g1.getCount();
        });
        for (GenreCount genreCount : genremap) {
            ans.put(genreCount.getName(), genreCount.getCount());
        }
        return ans;
    }
    public Map<List<String>, Integer> getCoStarCount(){
        Map<List<String>, Integer> CoStarmap = new HashMap<List<String>, Integer>();
        List<CoStarCount> res = new ArrayList<CoStarCount>();
        Map<List<String>, Integer> ans = new HashMap<List<String>, Integer>();
        movies.forEach(a -> {
            List<String> Costar_1 = new ArrayList<>(Arrays.asList(a.getStars()[0], a.getStars()[1]));
            List<String> Costar_2 = new ArrayList<>(Arrays.asList(a.getStars()[0], a.getStars()[2]));
            List<String> Costar_3 = new ArrayList<>(Arrays.asList(a.getStars()[0], a.getStars()[3]));
            List<String> Costar_4 = new ArrayList<>(Arrays.asList(a.getStars()[1], a.getStars()[2]));
            List<String> Costar_5 = new ArrayList<>(Arrays.asList(a.getStars()[1], a.getStars()[3]));
            List<String> Costar_6 = new ArrayList<>(Arrays.asList(a.getStars()[2], a.getStars()[3]));
            CoStarmap.put(Costar_1, CoStarmap.getOrDefault(Costar_1, 0) + 1);
            CoStarmap.put(Costar_2, CoStarmap.getOrDefault(Costar_2, 0) + 1);
            CoStarmap.put(Costar_3, CoStarmap.getOrDefault(Costar_3, 0) + 1);
            CoStarmap.put(Costar_4, CoStarmap.getOrDefault(Costar_4, 0) + 1);
            CoStarmap.put(Costar_5, CoStarmap.getOrDefault(Costar_5, 0) + 1);
            CoStarmap.put(Costar_6, CoStarmap.getOrDefault(Costar_6, 0) + 1);
        });
        Set<List<String>> set = CoStarmap.keySet();
        for(List<String> strs : set){
            res.add(new CoStarCount(strs, CoStarmap.get(strs)));
        }
        res.sort((c1, c2) -> {
            if (c1.getCount() == c2.getCount()) {
                if (c1.getStarsName().get(0).equals(c2.getStarsName().get(0))) {
                    return c1.getStarsName().get(1).compareTo(c2.getStarsName().get(1));
                }
                return c1.getStarsName().get(0).compareTo(c2.getStarsName().get(0));
            }
            return c2.getCount() - c1.getCount();
        });
        for(CoStarCount coStarCount : res){
            ans.put(coStarCount.getStarsName(), coStarCount.getCount());
        }
        return ans;
    }
    public List<String> getTopMovies(int top_k, String by){
        List<String> ans = new ArrayList<String>();
        if(by.equals("runtime")){
            movies.sorted((o1, o2) -> {
                if(o1.getRuntime() == o2.getRuntime())
                    return o1.getSeries_Title().compareTo(o2.getSeries_Title());
                return o2.getRuntime() - o1.getRuntime();
            }).limit(top_k).forEach(a -> {
                ans.add(a.getSeries_Title());
            });
        }else{
            movies.sorted((o1, o2) -> {
                if(o1.getOverview().length() == o2.getOverview().length())
                    return o1.getSeries_Title().compareTo(o2.getSeries_Title());
                return o2.getOverview().length() - o1.getOverview().length();
            }).limit(top_k).forEach(a -> {
                ans.add(a.getSeries_Title());
            });
        }
        return ans;
    }
    public List<String> getTopStars(int top_k, String by){
        List<String> ans = new ArrayList<String>();
        List<Star> starmap = new ArrayList<Star>();
        if(by.equals("rating")){
            Map<String,List<Float>> starRating =new HashMap<>();
            movies.forEach(a -> {
                starRating.put(a.getStars()[0], new ArrayList<Float>());
                starRating.put(a.getStars()[1], new ArrayList<Float>());
                starRating.put(a.getStars()[2], new ArrayList<Float>());
                starRating.put(a.getStars()[3], new ArrayList<Float>());
            });
            movies.forEach(a -> {
                starRating.get(a.getStars()[0]).add(a.getIMDB_Rating());
                starRating.get(a.getStars()[1]).add(a.getIMDB_Rating());
                starRating.get(a.getStars()[2]).add(a.getIMDB_Rating());
                starRating.get(a.getStars()[3]).add(a.getIMDB_Rating());
            });
            Set<String> set = starRating.keySet();
            for(String str : set){
                List<Float> temp = starRating.get(str);
                double rating = 0;
                for(double rat : temp){
                    rating += rat;
                }
                rating = rating / temp.size();
                starmap.add(new Star(str, rating));
            }
            starmap.sort((s1, s2) -> {
                double s1Rating = s1.getRating();
                double s2Rating = s2.getRating();
                if (s1Rating == s2Rating) {
                    return s1.getName().compareTo(s2.getName());
                }
                return (int) (s2Rating - s1Rating);
            });
            for(int i = 0 ; i < top_k ; i++){
                ans.add(starmap.get(i).getName());
            }
        }else{
            Map<String,List<Long>> starRating =new HashMap<>();
            movies.forEach(a -> {
                starRating.put(a.getStars()[0], new ArrayList<Long>());
                starRating.put(a.getStars()[1], new ArrayList<Long>());
                starRating.put(a.getStars()[2], new ArrayList<Long>());
                starRating.put(a.getStars()[3], new ArrayList<Long>());
            });
            movies.forEach(a -> {
                starRating.get(a.getStars()[0]).add(a.getGross());
                starRating.get(a.getStars()[1]).add(a.getGross());
                starRating.get(a.getStars()[2]).add(a.getGross());
                starRating.get(a.getStars()[3]).add(a.getGross());
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
            starmap.sort((s1, s2) -> {
                double s1Gross = s1.getGross();
                double s2Gross = s2.getGross();
                if(s1Gross == s2Gross){
                    return s1.getName().compareTo(s2.getName());
                }
                return (int) (s2Gross - s1Gross);
            });
            for(int i = 0 ; i < top_k ; i++){
                ans.add(starmap.get(i).getName());
            }
        }
        return ans;
    }
    public List<String> searchMovies(String genre, float min_rating, int max_runtime){
        List<String> ans = new ArrayList<String>();
        movies.filter(a -> {
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
        return  ans;
    }
}