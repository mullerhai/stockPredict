package com.network.meixin;

/**
 * Created by muller on 6/26/17.
 */
public class DailyData {

    public DailyData() {
    }

    @Override
    public String toString() {

        StringBuilder sb=new StringBuilder();
        sb.append("开盘价="+this.openPrice+", ");
        sb.append("收盘价 ="+this.closePrice+", ");
        sb.append(" 最高价=" +this.maxPrice+", ");
        sb.append(" 最低价="+this.minPrice+" ,   ");
        sb.append("成交量 ="+ this.turnover+",  ");
        sb.append(" 成交额 ="+this.volume);

        return sb.toString();
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    private double openPrice;


    public void setClosePrice(double closePrice) {
        this.closePrice = closePrice;
    }

    private double closePrice ;

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {

        this.maxPrice = maxPrice;
    }

    private double  maxPrice;

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    private double  minPrice ;

    public double getTurnover() {
        return turnover;
    }

    public void setTurnover(double turnover) {
        this.turnover = turnover;
    }

    private  double turnover;

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    private  double  volume;

    public double getOpenPrice() {
        return openPrice;
    }
    public double getClosePrice() {
        return closePrice;
    }
}
