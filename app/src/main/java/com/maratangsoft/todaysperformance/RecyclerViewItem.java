package com.maratangsoft.todaysperformance;

public class RecyclerViewItem {
    String mt20id;      //공연 ID
    String prfnm;       //공연명
    String genrenm;     //공연장르명
    String prfstate;    //공연상태 : Search전용
    String prfpdfrom;   //공연시작일
    String prfpdto;     //공연종료일
    String poster;      //포스터주소
    String fcltynm;     //공연시설명
    String area;        //지역 : Ranking전용
    String prfdtcnt;    //상연횟수 : Ranking전용
    String rnum;        //순위 : Ranking전용
    String seatcnt;     //좌석수 : Ranking전용

    //Favorite 목록용 객체생성
    public RecyclerViewItem(String mt20id, String prfnm, String genrenm, String prfpdfrom, String prfpdto, String poster, String fcltynm) {
        this.mt20id = mt20id;
        this.prfnm = prfnm;
        this.genrenm = genrenm;
        this.prfpdfrom = prfpdfrom;
        this.prfpdto = prfpdto;
        this.poster = poster;
        this.fcltynm = fcltynm;
    }

    public RecyclerViewItem() {
    }
}
