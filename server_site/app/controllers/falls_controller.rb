class FallsController < ApplicationController
  def index
    @falls = Fall.find(:all, :limit => 20, :order => "created_at desc")
    session[:freshness] = @falls.first.created_at
  end
  
  def refresh
    render :text => Fall.count(:conditions => ["created_at > ?", session[:freshness].to_s(:db)]).to_s
  end

  def create
    fall = Fall.create :details => {:datetime => Time.parse(params[:datetime].to_s),
                                    :root_sum_squares => params[:rss],
                                    :vertical_velocity => params[:vve],
                                    :latitude => params[:lat], 
                                    :longitude => params[:lon]}
    render :text => "fall_created"
  end
  
  def show
    @fall = Fall.find_by_id params[:id]
  end

end
