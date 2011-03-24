class FallsController < ApplicationController
  def index
    @falls = Fall.find(:all, :limit => 20, :order => "created_at desc")
  end

  def show
  end

  def create
    fall = Fall.create :details => {:datetime => Time.parse(params[:datetime].to_s),
                                    :root_sum_squares => params[:rss],
                                    :vertical_velocity => params[:vve],
                                    :latitude => params[:lat], 
                                    :longitude => params[:lon]}
    render :text => "fall_created"
  end

end
