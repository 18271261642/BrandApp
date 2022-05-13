package phone.gym.jkcq.com.socialmodule.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.LoadImageUtil;
import brandapp.isport.com.basicres.commonutil.TokenUtil;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import phone.gym.jkcq.com.socialmodule.R;
import phone.gym.jkcq.com.socialmodule.bean.RankInfo;


public class RopeRankAdapter extends BaseQuickAdapter<RankInfo, BaseViewHolder> {

    public RopeRankAdapter(List<RankInfo> data) {
        super(R.layout.item_rope_rank, data);
        addChildClickViewIds(R.id.iv_like);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, RankInfo rankInfo) {


        int ranke = 0;
        try {
            ranke = Integer.parseInt(rankInfo.getRankingNo());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (rankInfo.isWhetherPraise()) {
                holder.setImageResource(R.id.iv_like, R.drawable.icon_rope_like_unselected);
            } else {
                holder.setImageResource(R.id.iv_like, R.drawable.icon_rope_unlike_unselected);
            }

            holder.setText(R.id.iv_like_number, "" + rankInfo.getPraiseNums());
            if (TokenUtil.getInstance().getPeopleIdInt(BaseApp.instance).equals(rankInfo.getUserId())) {
                holder.setText(R.id.tv_nickname, rankInfo.getNickName() + UIUtils.getString(R.string.rope_ranking_me));
            } else {
                holder.setText(R.id.tv_nickname, rankInfo.getNickName());
            }
            holder.setText(R.id.tv_rank, rankInfo.getRankingNo());

            switch (ranke) {
                case 1:
                    holder.setBackgroundResource(R.id.tv_rank, R.drawable.shape_rope_ranking_one);
                    break;
                case 2:
                    holder.setBackgroundResource(R.id.tv_rank, R.drawable.shape_rope_ranking_two);
                    break;
                case 3:
                    holder.setBackgroundResource(R.id.tv_rank, R.drawable.shape_rope_ranking_three);
                    break;
                default:
                    holder.setBackgroundResource(R.id.tv_rank, R.drawable.shape_rope_ranking_other);
                    break;
            }
            if (rankInfo.getMotionType() == 6) {
                holder.setText(R.id.tv_count, "" + rankInfo.getTotalNum());
            } else if (rankInfo.getMotionType() == 7) {
                if (rankInfo.getTotalNum() != 0) {
                    holder.setText(R.id.tv_count, getRopeFormatTimehhmmss(rankInfo.getTotalNum()));
                } else {
                    holder.setText(R.id.tv_count, "00:00:00");
                }

            }

            LoadImageUtil.getInstance().loadCirc(getContext(), rankInfo.getHeadUrl(), holder.getView(R.id.circle_view_head));
        }


    }

    public static String getRopeFormatTimehhmmss(long time) {

        long sec = time % 60;
        long min = time / 60;
        long hour = min / 60;
        min = min % 60;
        String strHour = String.format("%02d", hour);
        String strMinute = String.format("%02d", min);
        String StrSecd = String.format("%02d", sec);
        return strHour + ":" + strMinute + ":" + StrSecd;
    }

}
