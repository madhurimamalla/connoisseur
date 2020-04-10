package mmalla.android.com.connoisseur.recommendations.engine;

public interface IMapper<From, To> {

    To map(From from);
}